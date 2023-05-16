package com.example.dgkotlin.data.model

data class User (
    val id: Long? = null,
    val email: String? = null,
    val password: String? = null,
    val roles: Array<String> = arrayOf(),
    val token: String? = null,
    val type: String? = null,
    val username: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (email != other.email) return false
        if (password != other.password) return false
        if (!roles.contentEquals(other.roles)) return false
        if (token != other.token) return false
        if (type != other.type) return false
        if (username != other.username) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (password?.hashCode() ?: 0)
        result = 31 * result + roles.contentHashCode()
        result = 31 * result + (token?.hashCode() ?: 0)
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (username?.hashCode() ?: 0)
        return result
    }
}