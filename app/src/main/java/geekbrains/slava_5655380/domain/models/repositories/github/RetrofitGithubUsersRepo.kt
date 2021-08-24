package geekbrains.slava_5655380.domain.models.repositories.github

import geekbrains.slava_5655380.domain.models.networkstatus.INetworkStatus
import geekbrains.slava_5655380.domain.models.repositories.github.repository.GithubRepository
import geekbrains.slava_5655380.domain.models.repositories.github.repository.RoomGithubRepository
import geekbrains.slava_5655380.domain.models.repositories.github.user.GithubUser
import geekbrains.slava_5655380.domain.models.repositories.github.user.RoomGithubUser
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class RetrofitGithubUsersRepo(
    private val api: IDataSource,
    private val networkStatus: INetworkStatus,
    private val usersCache: IGithubCache
) : IGithubUsersRepo {
    override fun getUsers() = networkStatus.isOnlineSingle().flatMap { isOnline ->
        if (isOnline) {
            api.getUsers()
                .flatMap { users ->
                    Single.fromCallable {
                        usersCache.cacheUsers(users)
                        users
                    }
                }
        } else {
            Single.fromCallable {
                usersCache.getUsersCache()
            }
        }
    }.subscribeOn(Schedulers.io())

    override fun getRepositories(user: GithubUser) =
        networkStatus.isOnlineSingle().flatMap { isOnline ->
            if (isOnline) {
                user.repos_url?.let { url ->
                    api.getRepositories(url)
                        .flatMap { repositories ->
                            Single.fromCallable {
                                usersCache.cacheRepositories(repositories, user)
                                repositories
                            }
                        }
                } ?: Single.error<List<GithubRepository>>(RuntimeException("User has no repos url"))
                    .subscribeOn(Schedulers.io())
            } else {
                Single.fromCallable {
                    usersCache.getRepositories(user)
                }
            }
        }.subscribeOn(Schedulers.io())
}