package io.prhunter.api.contract

import io.prhunter.api.bounty.BountyRepository
import io.prhunter.api.bounty.BountyStatus
import io.prhunter.api.github.GithubAppService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.exceptions.ContractCallException

@Service
@Profile("!test")
class EthContractService(
    bountyRepository: BountyRepository,
    githubAppService: GithubAppService,
    lazyGasProvider: LazyGasProvider,
    @Value("\${crypto.alchemyUrl}") val alchemyUrl: String,
    @Value("\${crypto.ethPkey}") val ethPrivateKey: String,
    @Value("\${crypto.bountyFactoryEthAddress}") val bountyFactoryEthAddress: String
) : ContractService(bountyRepository, githubAppService, lazyGasProvider) {

    // TODO extract chain info into separate bean that's passed around
    // so that we can inject it as required (ex. for comments and urls)

    final override val web3j = Web3j.build(HttpService(alchemyUrl))
    final override val credentials = Credentials.create(ethPrivateKey)
    final override val bountyFactory: BountyFactory = BountyFactory.load(
        bountyFactoryEthAddress,
        web3j,
        credentials,
        lazyGasProvider
    )

}