package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.DeliveryAddress;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.dto.AddressChangeDto;
import com.shop.onlyfit.dto.AddressDto;
import com.shop.onlyfit.exception.AddressNotFoundException;
import com.shop.onlyfit.exception.LoginIdNotFoundException;
import com.shop.onlyfit.repository.DeliveryAddressRepository;
import com.shop.onlyfit.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DeliveryAddressServiceImpl implements DeliveryAddressService {

    private final UserRepository userRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;

    public DeliveryAddressServiceImpl(UserRepository userRepository, DeliveryAddressRepository deliveryAddressRepository) {
        this.userRepository = userRepository;
        this.deliveryAddressRepository = deliveryAddressRepository;
    }

    @Transactional
    @Override
    public void registerAddress(String loginId, AddressDto addressDto) {
        User findUser = userRepository.findByLoginId(loginId).orElseThrow(
                () -> new LoginIdNotFoundException("해당하는 회원이 존재하지 않습니다")
        );
        DeliveryAddress deliveryAddress = new DeliveryAddress();

        deliveryAddress.setRecipient(addressDto.getRecipient());
        deliveryAddress.setCity(addressDto.getCity());
        deliveryAddress.setStreet(addressDto.getStreat());
        deliveryAddress.setZipcode(addressDto.getZipcode());
        deliveryAddress.setAddressPhoneNumber(addressDto.getAddressPhoneNumber());
        deliveryAddress.setAddressHomePhoneNumber(addressDto.getAddressHomePhoneNumber());
        deliveryAddress.setPlaceName(addressDto.getPlaceName());
        deliveryAddress.setUser(findUser);

        deliveryAddressRepository.save(deliveryAddress);
    }

    @Override
    public AddressChangeDto showAddressToChange(Long id) {

        DeliveryAddress findDeliveryAddress = deliveryAddressRepository.findById(id).orElseThrow(
                () -> new AddressNotFoundException("해당하는 주소가 존재하지 않습니다")
        );
        AddressChangeDto addressChangeDto = new AddressChangeDto();

        String[] addressHomePhoneNumber = findDeliveryAddress.getAddressHomePhoneNumber().split(",");
        String[] addressPhoneNumber = findDeliveryAddress.getAddressPhoneNumber().split(",");

        addressChangeDto.setId(findDeliveryAddress.getId());
        addressChangeDto.setPlaceName(findDeliveryAddress.getPlaceName());
        addressChangeDto.setRecipient(findDeliveryAddress.getRecipient());
        addressChangeDto.setCity(findDeliveryAddress.getCity());
        addressChangeDto.setZipcode(findDeliveryAddress.getZipcode());
        addressChangeDto.setStreat(findDeliveryAddress.getStreet());
        addressChangeDto.setAddressPhoneNumber(addressPhoneNumber);
        addressChangeDto.setAddressHomePhoneNumber(addressHomePhoneNumber);

        return addressChangeDto;
    }


    @Override
    public List<DeliveryAddress> getDeliveryAddressByLoginId(String loginId) {

        return deliveryAddressRepository.findAllByUserLoginId(loginId);
    }
//    회원에게 등록된 주소 모두 표시

    @Override
    public void deleteAddressById(Long id) {
        deliveryAddressRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void updateDeliveryAddress(Long id, AddressChangeDto addressChangeDto) {

        Optional<DeliveryAddress> findDeliveryAddress = deliveryAddressRepository.findById(id);

        DeliveryAddress deliveryAddress = findDeliveryAddress.orElseThrow(
                () -> new AddressNotFoundException("해당하는 주소가 존재하지 않습니다")
        );

        String addressPhoneNumberResult = addressChangeDto.getAddressPhoneNumber()[0] + "," + addressChangeDto.getAddressPhoneNumber()[1] + "," + addressChangeDto.getAddressPhoneNumber()[2];
        String addressHomePhoneNumberResult = addressChangeDto.getAddressHomePhoneNumber()[0] + "," + addressChangeDto.getAddressHomePhoneNumber()[1] + "," + addressChangeDto.getAddressHomePhoneNumber()[2];

        deliveryAddress.setPlaceName(addressChangeDto.getPlaceName());
        deliveryAddress.setRecipient(addressChangeDto.getRecipient());
        deliveryAddress.setAddressHomePhoneNumber(addressHomePhoneNumberResult);
        deliveryAddress.setAddressPhoneNumber(addressPhoneNumberResult);
        deliveryAddress.setZipcode(addressChangeDto.getZipcode());
        deliveryAddress.setCity(addressChangeDto.getCity());
        deliveryAddress.setStreet(addressChangeDto.getStreat());
    }
}
