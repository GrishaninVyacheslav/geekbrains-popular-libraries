package geekbrains.slava_5655380.domain.models.githubusers

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GithubUser(
    @Expose val id: String,
    @Expose val login: String,
    @Expose val avatarUrl: String? = null,
    @Expose val repos_url: String? = null
) : Parcelable