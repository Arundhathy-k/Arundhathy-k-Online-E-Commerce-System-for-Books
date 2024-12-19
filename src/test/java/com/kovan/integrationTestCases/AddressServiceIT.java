package com.kovan.integrationTestCases;

import com.kovan.entities.Address;
import com.kovan.repository.AddressRepository;
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
    private AddressRepository addressRepository;

    @AfterEach
    void cleanUp() {
        addressRepository.deleteAll();
    }

    private final Address address = Address.builder()
            .street("123 Test St")
            .city("Test City")
            .state("Test State")
            .postalCode("12345")
            .country("Test Country")
            .build();

    @Test
    void testAddAddress() {
        Address savedAddress = addressService.addAddress(address);

        assertNotNull(savedAddress);
        assertNotNull(savedAddress.getAddressId());
        assertEquals("123 Test St", savedAddress.getStreet());
        assertEquals("Test City", savedAddress.getCity());
        assertEquals("Test State", savedAddress.getState());
        assertEquals("12345", savedAddress.getPostalCode());
        assertEquals("Test Country", savedAddress.getCountry());
    }

    @Test
    void testGetAddressesById() {
        Address savedAddress = addressRepository.save(address);

        Address fetchedAddress = addressService.getAddressesById(savedAddress.getAddressId());

        assertNotNull(fetchedAddress);
        assertEquals(savedAddress.getAddressId(), fetchedAddress.getAddressId());
        assertEquals("123 Test St", fetchedAddress.getStreet());
    }

    @Test
    void testGetAllAddresses() {
        Address address1 = Address.builder()
                .street("Street 1")
                .city("City 1")
                .state("State 1")
                .postalCode("11111")
                .country("Country 1")
                .build();
        Address address2 = Address.builder()
                .street("Street 2")
                .city("City 2")
                .state("State 2")
                .postalCode("22222")
                .country("Country 2")
                .build();

        addressRepository.save(address1);
        addressRepository.save(address2);

        List<Address> addresses = addressService.getAllAddresses();

        assertNotNull(addresses);
        assertEquals(2, addresses.size());
    }

    @Test
    void testUpdateAddress() {
        Address savedAddress = addressRepository.save(address);

        Address updatedAddress = Address.builder()
                .street("New Street")
                .city("New City")
                .state("New State")
                .postalCode("11111")
                .country("New Country")
                .build();

        Address result = addressService.updateAddress(savedAddress.getAddressId(), updatedAddress);

        assertNotNull(result);
        assertEquals("New Street", result.getStreet());
        assertEquals("New City", result.getCity());
        assertEquals("New State", result.getState());
        assertEquals("11111", result.getPostalCode());
        assertEquals("New Country", result.getCountry());
    }

    @Test
    void testUpdateAddressThrowsException() {
        Long invalidAddressId = 999L;

        Address updatedAddress = Address.builder()
                .street("New Street")
                .city("New City")
                .state("New State")
                .postalCode("11111")
                .country("New Country")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> addressService.updateAddress(invalidAddressId, updatedAddress));

        assertEquals("Address not found with ID: " + invalidAddressId, exception.getMessage());
    }

    @Test
    void testDeleteAddress() {
        Address savedAddress = addressRepository.save(address);

        Long addressId = savedAddress.getAddressId();
        assertTrue(addressRepository.existsById(addressId));

        addressService.deleteAddress(addressId);

        assertFalse(addressRepository.existsById(addressId));
    }

    @Test
    void testDeleteAddressThrowsException() {
        Long invalidAddressId = 999L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> addressService.deleteAddress(invalidAddressId));

        assertEquals("Address not found with ID: " + invalidAddressId, exception.getMessage());
    }
}
