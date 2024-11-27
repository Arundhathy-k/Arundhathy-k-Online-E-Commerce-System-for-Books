package com.kovan.integrationTestCases;

import com.kovan.entities.Address;
import com.kovan.entities.User;
import com.kovan.repository.AddressRepository;
import com.kovan.repository.UserRepository;
import com.kovan.service.AddressService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;

@SpringBootTest
@Transactional
class AddressServiceIT {

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @AfterEach
    void cleanUp() {
        addressRepository.deleteAll();
        userRepository.deleteAll();
    }

    User user = User.builder()
            .firstName("Test User")
            .email("Test@gmail.com")
            .build();
    Address address = Address.builder()
            .street("123 Test St")
            .city("Test City")
            .state("Test State")
            .user(user)
            .postalCode("12345")
            .country("Test Country")
            .build();

    @Test
    void testAddAddress() {

        user = userRepository.save(user);

        Address savedAddress = addressService.addAddress(user.getUserId(), address);

        assertNotNull(savedAddress);
        assertNotNull(savedAddress.getAddressId());
        assertEquals("123 Test St", savedAddress.getStreet());
        assertEquals(user.getUserId(), savedAddress.getUser().getUserId());
    }

    @Test
    void testGetAddressesByUserId() {

        user = userRepository.save(user);
        addressRepository.save(address);

        List<Address> addresses = addressService.getAddressesByUserId(user.getUserId());
        assertNotNull(addresses);
        assertEquals(1, addresses.size());
    }

    @Test
    void testUpdateAddress() {

        user = userRepository.save(user);

        address = addressRepository.save(address);

        Address updatedAddress = Address.builder()
                .street("New Street")
                .city("New City")
                .state("New State")
                .postalCode("11111")
                .country("New Country")
                .build();

        Address savedAddress = addressService.updateAddress(address.getAddressId(), updatedAddress);

        assertNotNull(savedAddress);
        assertEquals("New Street", savedAddress.getStreet());
        assertEquals("New City", savedAddress.getCity());
        assertEquals("New State", savedAddress.getState());
        assertEquals("11111", savedAddress.getPostalCode());
        assertEquals("New Country", savedAddress.getCountry());
    }

    @Test
    void testDeleteAddress() {

        user = userRepository.save(user);

        address = addressRepository.save(address);

        Long addressId = address.getAddressId();
        assertTrue(addressRepository.existsById(addressId));

        addressService.deleteAddress(addressId);

        assertFalse(addressRepository.existsById(addressId));
    }

    @Test
    void testGetAddressesById() {

        user = userRepository.save(user);

        address = addressRepository.save(address);

        Address fetchedAddress = addressService.getAddressesById(address.getAddressId());

        assertNotNull(fetchedAddress);
        assertEquals(address.getAddressId(), fetchedAddress.getAddressId());
        assertEquals("123 Test St", fetchedAddress.getStreet());
    }
}
