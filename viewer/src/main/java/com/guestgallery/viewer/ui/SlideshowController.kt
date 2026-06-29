package com.guestgallery.viewer.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val MILLIS_PER_SECOND = 1_000L

/**
 * Controls automatic slideshow advancement through pages.
 *
 * Runs a coroutine-based delay loop that invokes [onAdvance] with the next
 * page index. Supports looping and clean cancellation via [stop].
 *
 * @param scope          Coroutine scope to launch the slideshow in.
 * @param delaySeconds   Seconds to wait between page transitions.
 * @param loop           Whether to wrap around after the last page.
 * @param pageCount      Total number of pages in the viewer.
 * @param onAdvance      Callback invoked with the next page index.
 * @param onFinished     Callback invoked when a non-looping slideshow reaches the end.
 */
class SlideshowController(
    private val scope: CoroutineScope,
    private val delaySeconds: Int,
    private val loop: Boolean,
    private val pageCount: Int,
    private val onAdvance: (nextIndex: Int) -> Unit,
    private val onFinished: () -> Unit = {},
) {
    private var job: Job? = null
    private var currentIndex: Int = 0

    /** Whether the slideshow is currently running. */
    val isRunning: Boolean
        get() = job?.isActive == true

    /**
     * Start the slideshow from [startIndex].
     * If already running, the previous slideshow is stopped first.
     */
    fun start(startIndex: Int = 0) {
        stop()
        currentIndex = startIndex
        job =
            scope.launch {
                while (true) {
                    delay(delaySeconds * MILLIS_PER_SECOND)
                    val next = currentIndex + 1
                    if (next >= pageCount) {
                        if (loop) {
                            currentIndex = 0
                            onAdvance(0)
                        } else {
                            onFinished()
                            break // reached the end — stop
                        }
                    } else {
                        currentIndex = next
                        onAdvance(next)
                    }
                }
            }
    }

    /** Stop the slideshow, cancelling any pending advancement. */
    fun stop() {
        job?.cancel()
        job = null
    }

    /** Update the current index externally (e.g. manual swipe during slideshow). */
    fun updateCurrentIndex(index: Int) {
        currentIndex = index
    }
}
