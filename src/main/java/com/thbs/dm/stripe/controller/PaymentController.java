package com.thbs.dm.stripe.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentIntentCollection;
import com.stripe.model.Refund;
import com.stripe.param.RefundCreateParams;

@Controller
public class PaymentController {
	
	@Value("${stripe.secret.key}") 
    private String stripeSecretKey;
	
	
	@PostMapping(value ="/cancelPayment", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
    public ResponseEntity<Object> cancelPayment(@RequestParam("paymentId") String paymentId, Model model) throws JsonProcessingException {
        Stripe.apiKey = stripeSecretKey;
        System.out.println("paymentId : "+paymentId);
        PaymentIntent updatedPaymentIntent = new PaymentIntent();
        try {
        	PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentId);
        	updatedPaymentIntent = paymentIntent.cancel();
        	System.out.println("updatedPaymentIntent : "+updatedPaymentIntent);
        	model.addAttribute("paymentResponse", updatedPaymentIntent);
        } catch (StripeException se) {
        	se.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.OK).body(updatedPaymentIntent.toString());
    }
	
	@GetMapping(value ="/listAllPayments", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
    public ResponseEntity<Object> listAllPayments() throws JsonProcessingException {
        Stripe.apiKey = stripeSecretKey;
        PaymentIntentCollection paymentIntents = new PaymentIntentCollection();
        try {
        	
        	Map<String, Object> params = new HashMap<>();
        	params.put("limit", 100);
        	paymentIntents =PaymentIntent.list(params);
        	System.out.println("paymentIntents : "+paymentIntents);
        } catch (StripeException se) {
        	se.printStackTrace();
        }
        	return ResponseEntity.status(HttpStatus.OK).body(paymentIntents.toString());
    }
	
	@PostMapping(value ="/createPayment", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
    public ResponseEntity<Object> createPayment() throws JsonProcessingException {
        Stripe.apiKey = stripeSecretKey;
        PaymentIntent paymentIntent = new PaymentIntent();
        try {
        	List<Object> paymentMethodTypes =
        			  new ArrayList<>();
        			paymentMethodTypes.add("card");
        			Map<String, Object> params = new HashMap<>();
        			params.put("amount", 2000);
        			params.put("currency", "inr");
        			params.put(
        			  "payment_method_types",
        			  paymentMethodTypes
        			);
        	paymentIntent = PaymentIntent.create(params);
        } catch (StripeException se) {
        	se.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.OK).body(paymentIntent.toString());
    }
	
	@PostMapping(value ="/refundPayment", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
    public ResponseEntity<Object> refundPayment(@RequestParam("paymentId") String paymentId) throws JsonProcessingException {
        Stripe.apiKey = stripeSecretKey;
        Refund refund = new Refund();
        try {
        	refund = Refund.create(RefundCreateParams.builder().setPaymentIntent(paymentId).build());
        } catch (StripeException se) {
        	se.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.OK).body(refund.toString());
    }
	
	@GetMapping(value ="/retrievePayment", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
    public ResponseEntity<Object> retrievePayment(@RequestParam("paymentId") String paymentId) throws JsonProcessingException {
        Stripe.apiKey = stripeSecretKey;
        System.out.println("paymentId : "+paymentId);
        PaymentIntent retrievedPaymentIntent = new PaymentIntent();
        try {
        	retrievedPaymentIntent = PaymentIntent.retrieve(paymentId);
        	System.out.println("retrievedPayment : "+retrievedPaymentIntent);
        } catch (StripeException se) {
        	se.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.OK).body(retrievedPaymentIntent.toString());
    }

}
