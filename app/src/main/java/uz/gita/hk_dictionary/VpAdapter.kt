package uz.gita.hk_dictionary

import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter

class VpAdapter(fm: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {
    private lateinit var listener: (()->Unit)


    override fun getItemCount(): Int  = 6
    override fun createFragment(position: Int): Fragment  = PageVp().apply {
        arguments = bundleOf(Pair("POS", position))
        letsListener = listener
    }

    fun setListener(block: ()->Unit){
        listener = block
    }


}
