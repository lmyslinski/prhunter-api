package io.prhunter.api.contract;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
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
public class Bounty extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_BOUNTYID = "bountyId";

    public static final String FUNC_CLAIMTIMEOUT = "claimTimeout";

    public static final String FUNC_EXPIRYTIMESTAMP = "expiryTimestamp";

    public static final String FUNC_GETBOUNTYVALUE = "getBountyValue";

    public static final String FUNC_PAYOUTBOUNTY = "payoutBounty";

    public static final Event BOUNTYCOMPLETED_EVENT = new Event("BountyCompleted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event BOUNTYTIMEOUTCLAIMED_EVENT = new Event("BountyTimeoutClaimed", 
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event DEPOSITRECEIVED_EVENT = new Event("DepositReceived", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected Bounty(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Bounty(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Bounty(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Bounty(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<BountyCompletedEventResponse> getBountyCompletedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(BOUNTYCOMPLETED_EVENT, transactionReceipt);
        ArrayList<BountyCompletedEventResponse> responses = new ArrayList<BountyCompletedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BountyCompletedEventResponse typedResponse = new BountyCompletedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.bountyAddress = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.balance = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<BountyCompletedEventResponse> bountyCompletedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, BountyCompletedEventResponse>() {
            @Override
            public BountyCompletedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(BOUNTYCOMPLETED_EVENT, log);
                BountyCompletedEventResponse typedResponse = new BountyCompletedEventResponse();
                typedResponse.log = log;
                typedResponse.bountyAddress = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.balance = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<BountyCompletedEventResponse> bountyCompletedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BOUNTYCOMPLETED_EVENT));
        return bountyCompletedEventFlowable(filter);
    }

    public List<BountyTimeoutClaimedEventResponse> getBountyTimeoutClaimedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(BOUNTYTIMEOUTCLAIMED_EVENT, transactionReceipt);
        ArrayList<BountyTimeoutClaimedEventResponse> responses = new ArrayList<BountyTimeoutClaimedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BountyTimeoutClaimedEventResponse typedResponse = new BountyTimeoutClaimedEventResponse();
            typedResponse.log = eventValues.getLog();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<BountyTimeoutClaimedEventResponse> bountyTimeoutClaimedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, BountyTimeoutClaimedEventResponse>() {
            @Override
            public BountyTimeoutClaimedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(BOUNTYTIMEOUTCLAIMED_EVENT, log);
                BountyTimeoutClaimedEventResponse typedResponse = new BountyTimeoutClaimedEventResponse();
                typedResponse.log = log;
                return typedResponse;
            }
        });
    }

    public Flowable<BountyTimeoutClaimedEventResponse> bountyTimeoutClaimedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BOUNTYTIMEOUTCLAIMED_EVENT));
        return bountyTimeoutClaimedEventFlowable(filter);
    }

    public List<DepositReceivedEventResponse> getDepositReceivedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(DEPOSITRECEIVED_EVENT, transactionReceipt);
        ArrayList<DepositReceivedEventResponse> responses = new ArrayList<DepositReceivedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DepositReceivedEventResponse typedResponse = new DepositReceivedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.senderAddress = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<DepositReceivedEventResponse> depositReceivedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, DepositReceivedEventResponse>() {
            @Override
            public DepositReceivedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(DEPOSITRECEIVED_EVENT, log);
                DepositReceivedEventResponse typedResponse = new DepositReceivedEventResponse();
                typedResponse.log = log;
                typedResponse.senderAddress = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<DepositReceivedEventResponse> depositReceivedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DEPOSITRECEIVED_EVENT));
        return depositReceivedEventFlowable(filter);
    }

    public RemoteFunctionCall<String> bountyId() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_BOUNTYID, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> claimTimeout() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CLAIMTIMEOUT, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> expiryTimestamp() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_EXPIRYTIMESTAMP, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getBountyValue() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETBOUNTYVALUE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> payoutBounty(String recipient) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_PAYOUTBOUNTY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, recipient)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static Bounty load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Bounty(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Bounty load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Bounty(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Bounty load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Bounty(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Bounty load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Bounty(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class BountyCompletedEventResponse extends BaseEventResponse {
        public String bountyAddress;

        public BigInteger balance;
    }

    public static class BountyTimeoutClaimedEventResponse extends BaseEventResponse {
    }

    public static class DepositReceivedEventResponse extends BaseEventResponse {
        public String senderAddress;

        public BigInteger amount;
    }
}
