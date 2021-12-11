package io.prhunter.api.github.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GithubRestClient(
    private val httpClient: HttpClient,
    private val objectMapper: ObjectMapper,
    @Value("\${github.baseUrl}") private val githubBaseUrl: String,
) {

    suspend fun listRepositories(installationToken: String): RepositoryList {
        val response = httpClient.get<HttpResponse>("$githubBaseUrl/installation/repositories") {
            headers {
                header("accept", "application/vnd.github.v3+json")
                header("Authorization", "Bearer $installationToken")
            }
        }

        if (response.status.value == 200) {
            return objectMapper.readValue(response.readText())
        } else {
            throw RuntimeException("Could not get repository data")
        }
    }

    suspend fun getIssue(repoOwner: String, repoName: String, issueNumber: Long, userToken: String): Issue {
        val response = httpClient.get<HttpResponse>("$githubBaseUrl/repos/$repoOwner/$repoName/issues/$issueNumber") {
            headers {
                header("accept", "application/vnd.github.v3+json")
                header("Authorization", "Bearer $userToken")
            }
        }

        if (response.status.value == 200) {
            return objectMapper.readValue(response.readText())
        } else {
            throw RuntimeException("Could not get issues")
        }
    }

    suspend fun listIssues(owner: String, repo: String, userToken: String): List<Issue> {
        val response = httpClient.get<HttpResponse>("$githubBaseUrl/repos/$owner/$repo/issues") {
            headers {
                header("accept", "application/vnd.github.v3+json")
                header("Authorization", "Bearer $userToken")
            }
        }

        if (response.status.value == 200) {
            return objectMapper.readValue(response.readText())
        } else {
            throw RuntimeException("Could not get issues")
        }
    }

    suspend fun getRepository(owner: String, repo: String, userToken: String): GHRepoData {
        val response = httpClient.get<HttpResponse>("$githubBaseUrl/repos/$owner/$repo") {
            headers {
                header("accept", "application/vnd.github.v3+json")
                header("Authorization", "Bearer $userToken")
            }
        }

        if (response.status.value == 200) {
            return objectMapper.readValue(response.readText())
        } else {
            throw RuntimeException("Could not get issue")
        }
    }

//    https://docs.github.com/en/rest/reference/repos#list-repositories-for-the-authenticated-user
    suspend fun listAuthenticatedUserRepos(userToken: String): List<GHRepoPermissionData> {
        val response = httpClient.get<HttpResponse>("$githubBaseUrl/user/repos") {
            headers {
                header("accept", "application/vnd.github.v3+json")
                header("Authorization", "Bearer $userToken")
            }
        }

        if (response.status.value == 200) {
            return objectMapper.readValue(response.readText())
        } else {
            throw RuntimeException("Could not get user repositories")
        }
    }


}