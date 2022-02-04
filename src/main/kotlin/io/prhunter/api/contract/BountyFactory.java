package io.prhunter.api.contract;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.*;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.4.1.
 */
@SuppressWarnings("rawtypes")
public class BountyFactory extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_CREATEBOUNTY = "createBounty";

    public static final String FUNC_GETTOTALBOUNTIES = "getTotalBounties";

    public static final Event BOUNTYCREATED_EVENT = new Event("BountyCreated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
    ;

    @Deprecated
    protected BountyFactory(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected BountyFactory(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected BountyFactory(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected BountyFactory(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<BountyCreatedEventResponse> getBountyCreatedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(BOUNTYCREATED_EVENT, transactionReceipt);
        ArrayList<BountyCreatedEventResponse> responses = new ArrayList<BountyCreatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BountyCreatedEventResponse typedResponse = new BountyCreatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.bountyAddress = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<BountyCreatedEventResponse> bountyCreatedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).filter(log -> {
            EventValues eventValues = staticExtractEventParameters(BOUNTYCREATED_EVENT, log);
            return eventValues != null && !eventValues.getNonIndexedValues().isEmpty();
        }).map(log -> {
            EventValues eventValues = extractEventParameters(BOUNTYCREATED_EVENT, log);
            BountyCreatedEventResponse typedResponse = new BountyCreatedEventResponse();
            typedResponse.log = log;
            typedResponse.bountyAddress = (String) eventValues.getNonIndexedValues().get(0).getValue();
            return typedResponse;
        });
    }

    public Flowable<BountyCreatedEventResponse> bountyCreatedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BOUNTYCREATED_EVENT));
        return bountyCreatedEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> createBounty(BigInteger _expiryTimestamp) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CREATEBOUNTY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_expiryTimestamp)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> getTotalBounties() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETTOTALBOUNTIES, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @Deprecated
    public static BountyFactory load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new BountyFactory(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static BountyFactory load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new BountyFactory(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static BountyFactory load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new BountyFactory(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static BountyFactory load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new BountyFactory(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class BountyCreatedEventResponse extends BaseEventResponse {
        public String bountyAddress;
    }
}
