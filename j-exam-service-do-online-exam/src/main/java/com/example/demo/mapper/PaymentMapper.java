package com.example.demo.mapper;

import com.example.demo.command.payment.ChargeResponse;
import com.stripe.model.Charge;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PaymentMapper {

    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    ChargeResponse toChargeResponse(Charge charge);
}
