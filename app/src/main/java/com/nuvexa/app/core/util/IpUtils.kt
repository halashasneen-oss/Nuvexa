package com.nuvexa.app.core.util

data class SubnetInfo(
    val networkAddress: String,
    val broadcastAddress: String,
    val subnetMask: String,
    val firstUsable: String,
    val lastUsable: String,
    val usableHostCount: Long,
)

private fun ipToLong(ip: String): Long? {
    val parts = ip.split(".")
    if (parts.size != 4) return null
    var result = 0L
    for (part in parts) {
        val octet = part.toIntOrNull() ?: return null
        if (octet !in 0..255) return null
        result = (result shl 8) or octet.toLong()
    }
    return result
}

private fun longToIp(value: Long): String {
    val v = value and 0xFFFFFFFFL
    return "${(v shr 24) and 0xFF}.${(v shr 16) and 0xFF}.${(v shr 8) and 0xFF}.${v and 0xFF}"
}

fun parseCidr(input: String): SubnetInfo? {
    val trimmed = input.trim()
    val slashIndex = trimmed.indexOf('/')
    if (slashIndex < 0) return null
    val ipPart = trimmed.substring(0, slashIndex)
    val prefix = trimmed.substring(slashIndex + 1).toIntOrNull() ?: return null
    if (prefix !in 0..32) return null

    val ipLong = ipToLong(ipPart) ?: return null
    val mask = if (prefix == 0) 0L else (0xFFFFFFFFL shl (32 - prefix)) and 0xFFFFFFFFL
    val network = ipLong and mask
    val broadcast = network or (mask.inv() and 0xFFFFFFFFL)

    val (first, last, usableCount) = when (prefix) {
        32 -> Triple(network, network, 1L)
        31 -> Triple(network, broadcast, 2L)
        else -> Triple(network + 1, broadcast - 1, (broadcast - network - 1).coerceAtLeast(0))
    }

    return SubnetInfo(
        networkAddress = longToIp(network),
        broadcastAddress = longToIp(broadcast),
        subnetMask = longToIp(mask),
        firstUsable = longToIp(first),
        lastUsable = longToIp(last),
        usableHostCount = usableCount,
    )
}
