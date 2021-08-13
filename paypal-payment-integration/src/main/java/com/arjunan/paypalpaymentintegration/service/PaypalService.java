package com.arjunan.paypalpaymentintegration.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arjunan.paypalpaymentintegration.dto.PaymentDetails;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

@Service
public class PaypalService {

	
    @Autowired(required = true)
    APIContext apiContext;

    public Payment paymentProcess(PaymentDetails paymentDetails) throws PayPalRESTException {
        Payment payment = null;
        Amount amount = null;
        Transaction transaction = null;
        List<Transaction> transactions = null;
        RedirectUrls redirectUrls = null;
        Payer payer = null;
        try {
            amount = new Amount();

            amount.setCurrency(paymentDetails.getCurrency());

            paymentDetails.setTotal(new BigDecimal(paymentDetails.getTotal()).setScale(2, RoundingMode.HALF_UP).doubleValue());

            amount.setTotal(String.format("%.2f", paymentDetails.getTotal()));

            transaction = new Transaction();
            transaction.setDescription(paymentDetails.getDescription());
            transaction.setAmount(amount);

            transactions = new ArrayList<>();
            transactions.add(transaction);

            payer = new Payer();
            payer.setPaymentMethod(paymentDetails.getMethod());

            payment = new Payment();
            payment.setIntent(paymentDetails.getIntent());
            payment.setPayer(payer);
            payment.setTransactions(transactions);

            redirectUrls = new RedirectUrls();
            redirectUrls.setCancelUrl(paymentDetails.getCancelUrl());
            redirectUrls.setReturnUrl(paymentDetails.getSuccessUrl());

            payment.setRedirectUrls(redirectUrls);
            
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return payment.create(apiContext);
    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = null;
        PaymentExecution paymentExecution = null;
        try {
            payment = new Payment();
            payment.setId(paymentId);

            paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(payerId);
            
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return payment.execute(apiContext, paymentExecution);
        
    }
}
