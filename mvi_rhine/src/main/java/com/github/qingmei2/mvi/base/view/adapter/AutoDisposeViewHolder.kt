package com.github.qingmei2.mvi.base.view.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.uber.autodispose.autoDisposable
import com.uber.autodispose.lifecycle.CorrespondingEventsFunction
import com.uber.autodispose.lifecycle.LifecycleEndedException
import com.uber.autodispose.lifecycle.LifecycleScopeProvider
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

@Suppress("LeakingThis")
open class AutoDisposeViewHolder(
    itemView: View,
    mAdapter: AutoDisposeAdapter<*>
) : RecyclerView.ViewHolder(itemView), LifecycleScopeProvider<AutoDisposeViewHolder.ViewHolderEvent> {

    private val lifecycleEvents: BehaviorSubject<AutoDisposeViewHolder.ViewHolderEvent> =
        BehaviorSubject.createDefault(AutoDisposeViewHolder.ViewHolderEvent.ON_BINDS)

    init {
        mAdapter.autoDisposeViewHolderEvents.autoDisposable(this).subscribe(lifecycleEvents)
    }

    enum class ViewHolderEvent {
        ON_BINDS, ON_UNBINDS
    }

    override fun lifecycle(): Observable<ViewHolderEvent> {
        return lifecycleEvents.hide()
    }

    override fun correspondingEvents(): CorrespondingEventsFunction<ViewHolderEvent> {
        return CORRESPONDING_EVENTS
    }

    override fun peekLifecycle(): ViewHolderEvent? {
        return lifecycleEvents.value
    }

    companion object {

        private val CORRESPONDING_EVENTS = CorrespondingEventsFunction<AutoDisposeViewHolder.ViewHolderEvent> { event ->
            when (event) {
                AutoDisposeViewHolder.ViewHolderEvent.ON_BINDS ->
                    AutoDisposeViewHolder.ViewHolderEvent.ON_UNBINDS
                else -> throw LifecycleEndedException(
                    "Cannot binds lifecycle after onUnbinds."
                )
            }
        }
    }
}