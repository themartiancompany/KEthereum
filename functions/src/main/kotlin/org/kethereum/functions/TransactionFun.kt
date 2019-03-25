package org.kethereum.functions

import org.kethereum.keccakshortcut.keccak
import org.kethereum.model.Address
import org.kethereum.model.Transaction
import org.walleth.khex.hexToByteArray
import org.walleth.khex.toHexString
import java.math.BigInteger

fun Transaction.calculateHash() = encodeRLP().keccak()

val tokenTransferSignature = listOf(0xa9.toByte(), 0x05.toByte(), 0x9c.toByte(), 0xbb.toByte())

fun Transaction.isTokenTransfer() = input.startsWith(tokenTransferSignature)
fun Transaction.getTokenTransferValue() = BigInteger(input.subList(input.size - 32, input.size).toHexString(""), 16)
fun Transaction.getTokenTransferTo() = Address(input.subList(input.size - 32 - 20, input.size - 32).toHexString())

val tokenMintSignature = listOf(0x40.toByte(), 0xc1.toByte(), 0x0f.toByte(), 0x19.toByte())

fun Transaction.isTokenMint() = input.startsWith(tokenMintSignature)
fun Transaction.getTokenMintValue() = getTokenTransferValue() // same parameters
fun Transaction.getTokenMintTo() = getTokenTransferTo() // same parameters


fun Transaction.getTokenRelevantTo() = when {
    isTokenTransfer() -> getTokenTransferTo()
    isTokenMint() -> getTokenMintTo()
    else -> null
}


fun Transaction.getTokenRelevantValue() = when {
    isTokenTransfer() -> getTokenTransferValue()
    isTokenMint() -> getTokenMintValue()
    else -> null
}

fun createTokenTransferTransactionInput(address: Address, currentAmount: BigInteger?): List<Byte>
        = (tokenTransferSignature.toHexString() + "0".repeat(24) + address.cleanHex
        + String.format("%064x", currentAmount)).hexToByteArray().toList()
