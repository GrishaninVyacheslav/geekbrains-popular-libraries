package geekbrains.slava_5655380.ui.views.fragments.user

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface UserView : MvpView {
    fun showUserData(data: String)
    fun init()
    fun updateList()
}