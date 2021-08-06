package io.prhunter.api.user

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class GithubUserController {

    @GetMapping("/repo")
    fun listRepositories(){

    }

//    @GetMapping("/repo/${id}/issues")
    fun listIssues(@PathVariable repoId: Long){

    }


}