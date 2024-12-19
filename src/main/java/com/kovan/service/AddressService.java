package com.kovan.service;

import com.kovan.entities.Address;
import com.kovan.repository.AddressRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Address getAddressesById(Long id) {
        return addressRepository.findById(id).orElseThrow(() -> new RuntimeException("Address not found!"));
    }
    public List<Address> getAllAddresses(){
        return addressRepository.findAll();
    }
    public Address addAddress(Address address) {

        return addressRepository.save(address);
    }

    public Address updateAddress(Long addressId, Address updatedAddress) {
        Address existingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address not found with ID: " + addressId));

        existingAddress.setStreet(updatedAddress.getStreet());
        existingAddress.setCity(updatedAddress.getCity());
        existingAddress.setState(updatedAddress.getState());
        existingAddress.setPostalCode(updatedAddress.getPostalCode());
        existingAddress.setCountry(updatedAddress.getCountry());

        return addressRepository.save(existingAddress);
    }

    public void deleteAddress(Long addressId) {
        if (!addressRepository.existsById(addressId)) {
            throw new IllegalArgumentException("Address not found with ID: " + addressId);
        }
        addressRepository.deleteById(addressId);
    }
}
