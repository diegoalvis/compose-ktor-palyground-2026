package product_detail_v2.data

sealed class ProductException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NetworkException(cause: Throwable) : ProductException("Network error occurred while fetching products", cause)
    class LocalSourceException(cause: Throwable) : ProductException("Error accessing local storage", cause)
    class MappingException(message: String) : ProductException(message)
    class UnknownException(cause: Throwable) : ProductException("An unexpected error occurred", cause)
}
