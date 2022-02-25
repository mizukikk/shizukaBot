package api

interface ResponseCallBack<T> {
    abstract fun success(response: T)
    abstract fun fail()
}