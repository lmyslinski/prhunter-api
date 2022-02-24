package io.prhunter.api.contract

import io.mockk.every
import io.mockk.mockk
import io.prhunter.api.bounty.BountyRepository
import io.prhunter.api.contract.abi.BountyFactory
import io.prhunter.api.contract.gas.LazyGasProvider
import io.prhunter.api.github.GithubAppService
import org.springframework.stereotype.Service
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import javax.annotation.PostConstruct

@Service("ethContractService")
class MockEthContractService(
    bountyRepository: BountyRepository,
    githubAppService: GithubAppService
) : ContractService(bountyRepository, githubAppService) {

    override val bountyFactory: BountyFactory = mockk()
    override val web3j: Web3j = mockk()
    override val credentials: Credentials = mockk()
    override val lazyGasProvider: LazyGasProvider = mockk()
    override val blockchainInfo: BlockchainInfo = mockk()

    @PostConstruct
    fun init(){
        every { bountyFactory.contractAddress }.returns("0x0")
    }

}