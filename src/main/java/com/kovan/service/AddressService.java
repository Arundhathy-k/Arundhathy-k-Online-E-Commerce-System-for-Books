package com.kovan.service;

import com.kovan.entities.Address;
import com.kovan.entities.User;
import com.kovan.repository.AddressRepository;
import com.kovan.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    public List<Address> getAddressesByUserId(Long userId) {
        return addressRepository.findByUserUserId(userId);
    }

    public Address getAddressesById(Long id) {
        return addressRepository.findById(id).orElseThrow(() -> new RuntimeException("Address not found!"));
    }

    public Address addAddress(Long userId, Address address) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        address.setUser(user);
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
