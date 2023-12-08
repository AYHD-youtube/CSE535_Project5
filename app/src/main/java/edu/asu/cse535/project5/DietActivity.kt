package edu.asu.cse535.project5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import org.json.JSONObject

class DietActivity : AppCompatActivity() {
    private val NUTRITIONIX_BASE_URL = "https://trackapi.nutritionix.com"
    private val NUTRITIONIX_INSTANT_SEARCH_ENDPOINT = "/v2/search/instant/?query="
    private val NUTRITIONIX_UPC_SEARCH_ENDPOINT = "/v2/search/item/?upc="

    // Not worried about exposing keys right now as I registered the account using public inbox
    private val NUTRITIONIX_APP_ID = "25538016"
    private val NUTRITIONIX_APP_KEY = "247d45a977d4ef2b246d1e95e2326cdd"
    lateinit var autoCompleteTextView : AutoCompleteTextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diet)

        // Barcode scanner button click event
        findViewById<Button>(R.id.scanBarcodeButton).setOnClickListener {
            val options = GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_UPC_A,
                    Barcode.FORMAT_UPC_E)
                .enableAutoZoom()
                .build()
            val scanner = GmsBarcodeScanning.getClient(this, options)

            scanner.startScan()
                .addOnSuccessListener { barcode ->
                    val rawValue: String? = barcode.rawValue
                    if (rawValue != null) {
                        Log.i("Barcode Scanner", rawValue)
                        fetchNutrientsData(rawValue)
                    } else {
                        showToast("Invalid scan")
                    }
                }
                .addOnCanceledListener {
                    // Task canceled
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                }
        }

        autoCompleteTextView = findViewById(R.id.mealNameAutoComplete)

        autoCompleteTextView.threshold = 3

        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val userInput = s.toString()
                if (userInput.length > 3) {
                    fetchSuggestions(userInput)
                }
            }
        })

        autoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val selectedItem = parent?.getItemAtPosition(position) as String
                val record : HashMap<String, String> = HashMap<String, String>()
                val entries : List<String> = selectedItem.split(":")
                record["meal"] = entries[0];
                record["calories"] = entries[2]

                persistData(record)
            }


        // Form submission button click event
        findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            val mealName = findViewById<EditText>(R.id.mealNameEditText).text.toString()
            val caloriesStr = findViewById<EditText>(R.id.caloriesEditText).text.toString()
            if (isEmptyOrNull(mealName) || isEmptyOrNull(caloriesStr)) {
                showToast("Please fill in all fields.")
            } else {
                val record : HashMap<String, String> = HashMap<String, String>()
                record["meal"] = mealName;
                record["calories"] = caloriesStr

                this.persistData(record)
            }
        }
    }

    private fun fetchSuggestions(query: String) {
        val url = "$NUTRITIONIX_BASE_URL$NUTRITIONIX_INSTANT_SEARCH_ENDPOINT${query.replace(" ", "%20")}"

        val request = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val suggestions = parseSuggestions(response)
                populateAutoComplete(suggestions)
            },
            Response.ErrorListener { error ->
                showToast("Error fetching suggestions: ${error.message}")
            }) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["x-remote-user-id"] = "0"
                headers["x-app-id"] = NUTRITIONIX_APP_ID
                headers["x-app-key"] = NUTRITIONIX_APP_KEY

                return headers
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun parseSuggestions(response: JSONObject): List<String> {
        val branded = response.optJSONArray("branded")
        val suggestions = mutableListOf<String>()
        branded?.let {
            for (i in 0 until branded.length()) {
                val item = branded.optJSONObject(i)
                val name = item?.optString("brand_name_item_name")
                val serving = item?.optString("serving_qty") + " " + item?.optString("serving_unit")
                val calories = item?.optString("nf_calories")
                item?.let {
                    suggestions.add("$name:$serving:$calories")
                }
            }
        }
        return suggestions
    }

    private fun populateAutoComplete(suggestions: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.select_dialog_item, suggestions)
        autoCompleteTextView.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }

    private fun fetchNutrientsData(upc: String) {
        val url = "$NUTRITIONIX_BASE_URL$NUTRITIONIX_UPC_SEARCH_ENDPOINT$upc"

        val request = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val nutrientsData = parseNutrientsData(response)
                val record : HashMap<String, String> = HashMap<String, String>()
                val entries : List<String> = nutrientsData.split(":")
                record["meal"] = entries[0];
                record["calories"] = entries[2]
            },
            Response.ErrorListener { error ->
                showToast("Error fetching suggestions: ${error.message}")
            }) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["x-remote-user-id"] = "0"
                headers["x-app-id"] = NUTRITIONIX_APP_ID
                headers["x-app-key"] = NUTRITIONIX_APP_KEY

                return headers
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun parseNutrientsData(response: JSONObject): String {
        val foods = response.optJSONArray("foods")

        val item = foods.optJSONObject(0)
        val brand = item?.optString("brand_name")
        val food = item?.optString("food_name")
        val serving = item?.optString("serving_qty") + " " + item?.optString("serving_unit")
        val calories = item?.optString("nf_calories")
        return "$brand $food:$serving:$calories"
    }

    private fun persistData(record : Map<String, String>) {
        // TODO : Integrate in Project 5 with Google Firestore
        // Logging for now to avoid having conflicts in future due to two Goggle services integration
        Log.i("Google Firestore", "Meal: ${record["meal"]}, Calories: ${record["calories"]}, Timestamp: ${System.currentTimeMillis()}");
    }

    private fun isEmptyOrNull(text: String):Boolean{
        return text == null || text.equals("")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}