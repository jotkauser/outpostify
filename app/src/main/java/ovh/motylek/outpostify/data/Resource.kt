package ovh.motylek.outpostify.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


inline fun <OutputType, ApiType> resource(
    mutex: Mutex = Mutex(),
    crossinline shouldFetch: suspend (OutputType) -> Boolean,
    crossinline fetch: suspend () -> ApiType,
    crossinline query: () -> Flow<OutputType>,
    crossinline saveFetchResult: suspend (old: OutputType, new: ApiType) -> Unit,
    crossinline filterResult: (OutputType) -> OutputType = { it }
) = flow {
    val data = query().first()
    if (shouldFetch(data)) {
        try {
            val newData = fetch()
            mutex.withLock { saveFetchResult(query().first(), newData) }
        } catch (throwable: Throwable) {
            throw throwable
        }
    }

    emitAll(query())
}.map { filterResult(it) }