package com.example.erp.service;

import com.example.erp.dto.PaymentRequestDTO;
import com.example.erp.dto.PaymentResponseDTO;
import java.util.List;

public interface PaymentService {
    PaymentResponseDTO create(PaymentRequestDTO dto);
    PaymentResponseDTO update(Long id, PaymentRequestDTO dto);
    PaymentResponseDTO getById(Long id);
    List<PaymentResponseDTO> getByInvoiceId(Long invoiceId);
}
