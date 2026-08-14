package com.nuvexa.app.core.model

/** Port/service names are technical protocol identifiers, conventionally left untranslated;
 * the surrounding screen chrome is fully localized. */
data class PortEntry(val port: Int, val service: String, val description: String)

val COMMON_PORTS = listOf(
    PortEntry(20, "FTP (data)", "File Transfer Protocol data channel"),
    PortEntry(21, "FTP (control)", "File Transfer Protocol control channel"),
    PortEntry(22, "SSH", "Secure Shell — encrypted remote login"),
    PortEntry(23, "Telnet", "Unencrypted remote login (legacy)"),
    PortEntry(25, "SMTP", "Simple Mail Transfer Protocol — sending email"),
    PortEntry(53, "DNS", "Domain Name System"),
    PortEntry(67, "DHCP (server)", "Dynamic Host Configuration Protocol, server side"),
    PortEntry(68, "DHCP (client)", "Dynamic Host Configuration Protocol, client side"),
    PortEntry(80, "HTTP", "Hypertext Transfer Protocol — unencrypted web"),
    PortEntry(110, "POP3", "Post Office Protocol v3 — receiving email"),
    PortEntry(123, "NTP", "Network Time Protocol"),
    PortEntry(143, "IMAP", "Internet Message Access Protocol — receiving email"),
    PortEntry(161, "SNMP", "Simple Network Management Protocol"),
    PortEntry(194, "IRC", "Internet Relay Chat"),
    PortEntry(389, "LDAP", "Lightweight Directory Access Protocol"),
    PortEntry(443, "HTTPS", "HTTP over TLS/SSL — encrypted web"),
    PortEntry(445, "SMB", "Server Message Block — Windows file sharing"),
    PortEntry(465, "SMTPS", "SMTP over TLS/SSL"),
    PortEntry(587, "SMTP (submission)", "Mail submission with authentication"),
    PortEntry(993, "IMAPS", "IMAP over TLS/SSL"),
    PortEntry(995, "POP3S", "POP3 over TLS/SSL"),
    PortEntry(1433, "MSSQL", "Microsoft SQL Server"),
    PortEntry(1521, "Oracle DB", "Oracle database default listener"),
    PortEntry(3306, "MySQL", "MySQL / MariaDB database"),
    PortEntry(3389, "RDP", "Remote Desktop Protocol"),
    PortEntry(5432, "PostgreSQL", "PostgreSQL database"),
    PortEntry(5672, "AMQP", "Advanced Message Queuing Protocol (e.g. RabbitMQ)"),
    PortEntry(6379, "Redis", "Redis in-memory data store"),
    PortEntry(8080, "HTTP (alt)", "Common alternate HTTP port"),
    PortEntry(8443, "HTTPS (alt)", "Common alternate HTTPS port"),
    PortEntry(27017, "MongoDB", "MongoDB database"),
)
