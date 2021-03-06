package geekbrains.slava_5655380.ui.presenters.user

import android.util.Log
import com.github.terrakok.cicerone.Router
import geekbrains.slava_5655380.domain.models.repositories.github.user.GithubUser
import geekbrains.slava_5655380.domain.models.repositories.github.IGithubUsersRepo
import geekbrains.slava_5655380.domain.models.repositories.github.repository.GithubRepository
import geekbrains.slava_5655380.ui.views.Screens
import geekbrains.slava_5655380.ui.views.fragments.user.UserView
import geekbrains.slava_5655380.ui.views.fragments.user.adapter.RepositoryItemView
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import moxy.MvpPresenter

class UserPresenter(
    private val userRepository: IGithubUsersRepo,
    private val router: Router,
    private val user: GithubUser,
    private var disposable: Disposable? = null,
    private val uiScheduler: Scheduler = AndroidSchedulers.mainThread(),
    val repositoryListPresenter: RepositoryListPresenter = RepositoryListPresenter()
) : MvpPresenter<UserView>() {
    class RepositoryListPresenter() : IRepositoriesListPresenter {
        val repositories = mutableListOf<GithubRepository>()
        override var itemClickListener: ((RepositoryItemView) -> Unit)? = null

        override fun getCount() = repositories.size

        override fun bindView(view: RepositoryItemView) {
            val repository = repositories[view.pos]
            with(view) {
                repository.name?.let { setName(it) }
                    ?: Log.i("[RepoListPresenter]", "$repository repository name is null")
                repository.description?.let { setDescription(it) }
                    ?: Log.i("[RepoListPresenter]", "$repository repository description is null")
            }
        }
    }

    private val observer = object : DisposableSingleObserver<List<GithubRepository>>() {
        override fun onSuccess(value: List<GithubRepository>) {
            repositoryListPresenter.repositories.addAll(value)
            viewState.updateList()
        }

        override fun onError(error: Throwable) {
            error.printStackTrace()
        }
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        user.login?.let { viewState.showUserData(it) }
        viewState.init()
        loadData()
        repositoryListPresenter.itemClickListener = { itemView ->
            with(repositoryListPresenter.repositories[itemView.pos]) {
                router.navigateTo(Screens.repository(this))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

    private fun loadData() {
        disposable = userRepository
            .getRepositories(user)
            .observeOn(uiScheduler)
            .subscribeWith(observer)
    }

    fun backPressed(): Boolean {
        router.exit()
        return true
    }
}