package edu.asu.cse535.project5.datamodel

data class NotiResponse(
    val canonical_ids: Int,
    val failure: Int,
    val multicast_id: Long,
    val results: List<Result>,
    val success: Int
) {
    data class Result(
        val message_id: String
    )
}