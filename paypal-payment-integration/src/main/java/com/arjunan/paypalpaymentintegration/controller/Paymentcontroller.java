package com.arjunan.paypalpaymentintegration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.arjunan.paypalpaymentintegration.dto.PaymentDetails;
import com.arjunan.paypalpaymentintegration.service.PaypalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;


@Controller
public class Paymentcontroller {

    @Autowired
    PaypalService paypalService;

    private static final String CANCEL_URL = "pay/cancel";
    private static final String SUCCESS_URL = "pay/success";
    @GetMapping("/")
    public String home() {
        return "home";
    }

    @PostMapping("/pay")
    public String payment(@ModelAttribute("order") PaymentDetails paymentDetails) {
        try {
        paymentDetails.setCancelUrl("http://localhost:9090/"+CANCEL_URL);
        paymentDetails.setSuccessUrl("http://localhost:9090/"+SUCCESS_URL);
        Payment payment = paypalService.paymentProcess(paymentDetails);
        for(Links links : payment.getLinks()){
            if(links.getRel().equals("approval_url")){
                return "redirect:"+ links.getHref();
            }
        }
        
         
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
         return "redirect:/";
    }

    @GetMapping(value = CANCEL_URL)
    public String cancelPay() {
        return "cancel";
    }

    @GetMapping(value = SUCCESS_URL)
    public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            System.out.println(payment.toJSON());
            if (payment.getState().equals("approved")) {
                return "success";
            }
        } catch (PayPalRESTException e) {
         System.out.println(e.getMessage());
        }
        return "redirect:/";
    }
}
