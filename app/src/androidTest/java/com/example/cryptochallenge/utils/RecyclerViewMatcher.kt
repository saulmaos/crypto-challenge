package com.example.cryptochallenge.utils

import android.content.res.Resources
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

class RecyclerViewMatcher(val recyclerViewId: Int = 0) {

    companion object {
        fun withRecyclerView(recyclerViewId: Int): RecyclerViewMatcher {
            return RecyclerViewMatcher(recyclerViewId)
        }

        fun hasItemCount(itemCount: Int): Matcher<View> {
            return object : BoundedMatcher<View, RecyclerView>(
                RecyclerView::class.java
            ) {

                override fun describeTo(description: Description) {
                    description.appendText("has $itemCount items")
                }

                override fun matchesSafely(view: RecyclerView): Boolean {
                    Log.d("RecyclerViewMatcher", "${view.adapter!!.itemCount}")
                    return view.adapter!!.itemCount == itemCount
                }
            }
        }
    }

    fun atPosition(position: Int): Matcher<View?> {
        return atPositionOnView(position, -1)
    }

    fun atPositionOnView(position: Int, targetViewId: Int): Matcher<View?> {
        return object : TypeSafeMatcher<View?>() {
            var resources: Resources? = null
            var childView: View? = null
            override fun describeTo(description: Description?) {
                var idDescription = recyclerViewId.toString()
                if (resources != null) {
                    idDescription = try {
                        resources!!.getResourceName(recyclerViewId)
                    } catch (var4: Resources.NotFoundException) {
                        String.format(
                            "%s (resource name not found)",
                            (Integer.valueOf(recyclerViewId))
                        )
                    }
                }
                description!!.appendText("with id: $idDescription")
            }

            override fun matchesSafely(view: View?): Boolean {
                resources = view!!.resources

                if (childView == null) {
                    val recyclerView = view.rootView.findViewById(recyclerViewId) as RecyclerView?
                    if (recyclerView != null && recyclerView.id == recyclerViewId) {
                        val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
                        if (viewHolder != null) {
                            childView = viewHolder.itemView
                        }
                    } else {
                        return false
                    }
                }

                return if (targetViewId == -1) {
                    view === childView
                } else {
                    val targetView = childView!!.findViewById<View>(targetViewId)
                    view === targetView
                }
            }
        }
    }
}
