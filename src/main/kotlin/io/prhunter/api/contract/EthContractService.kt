package io.prhunter.api.contract

import io.prhunter.api.bounty.BountyRepository
import io.prhunter.api.contract.abi.BountyFactory
import io.prhunter.api.contract.gas.GasPriceResolver
import io.prhunter.api.contract.gas.LazyGasProvider
import io.prhunter.api.github.GithubAppService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService

@Service
@Profile("!test")
class EthContractService(
    bountyRepository: BountyRepository,
    githubAppService: GithubAppService,
    ethBlockchainInfo: BlockchainInfo,
    gasPriceResolver: GasPriceResolver
) : ContractService(bountyRepository, githubAppService) {

    final override val web3j = Web3j.build(HttpService(ethBlockchainInfo.rpcUrl))
    final override val credentials = Credentials.create(ethBlockchainInfo.walletPkey)
    final override val lazyGasProvider: LazyGasProvider = LazyGasProvider(gasPriceResolver, ethBlockchainInfo)
    final override val blockchainInfo: BlockchainInfo = ethBlockchainInfo
    final override val bountyFactory: BountyFactory = BountyFactory.load(
        ethBlockchainInfo.bountyFactoryAddress,
        web3j,
        credentials,
        lazyGasProvider
    )

}