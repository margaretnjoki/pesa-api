package com.margaretnjoki.pesaapi.repository;

import com.margaretnjoki.pesaapi.model.MpesaTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MpesaTransactionRepository extends JpaRepository<MpesaTransaction, UUID> {
   Optional <MpesaTransaction> findByCheckoutRequestId(String checkoutRequestId);

    List<MpesaTransaction> findByPhoneNumberOrderByCreatedAtDesc(String phoneNumber);
}
