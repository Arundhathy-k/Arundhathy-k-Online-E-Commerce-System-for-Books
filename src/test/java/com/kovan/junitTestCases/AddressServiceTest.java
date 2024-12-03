package com.kovan.junitTestCases;

import com.kovan.entities.Address;
import com.kovan.repository.AddressRepository;
import com.kovan.service.AddressService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    private final Address address = Address.builder()
            .addressId(1L)
            .state("Karnataka")
            .street("R K Nagar")
            .city("Bangalore")
            .country("India")
            .postalCode("846467")
            .build();

    @Test
    void testAddAddress() {
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        Address result = addressService.addAddress(address);

        assertNotNull(result);
        assertEquals(address, result);
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void testGetAddressesById() {
        when(addressRepository.findById(address.getAddressId())).thenReturn(of(address));

        Address result = addressService.getAddressesById(address.getAddressId());

        assertNotNull(result);
        assertEquals(address, result);
        verify(addressRepository, times(1)).findById(address.getAddressId());
    }

    @Test
    void testGetAddressesByIdThrowsException() {
        when(addressRepository.findById(address.getAddressId())).thenReturn(empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> addressService.getAddressesById(address.getAddressId()));

        assertEquals("Address not found!", exception.getMessage());
        verify(addressRepository, times(1)).findById(address.getAddressId());
    }

    @Test
    void testGetAllAddresses() {
        List<Address> addressList = List.of(address);
        when(addressRepository.findAll()).thenReturn(addressList);

        List<Address> result = addressService.getAllAddresses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(address, result.get(0));
        verify(addressRepository, times(1)).findAll();
    }

    @Test
    void testUpdateAddress() {
        Address updatedAddress = Address.builder()
                .street("456 Elm St")
                .city("Shelbyville")
                .state("IL")
                .postalCode("62565")
                .country("USA")
                .build();

        when(addressRepository.findById(address.getAddressId())).thenReturn(of(address));
        when(addressRepository.save(any(Address.class))).thenReturn(updatedAddress);

        Address result = addressService.updateAddress(address.getAddressId(), updatedAddress);

        assertNotNull(result);
        assertEquals(updatedAddress.getStreet(), result.getStreet());
        assertEquals(updatedAddress.getCity(), result.getCity());
        verify(addressRepository, times(1)).findById(address.getAddressId());
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void testUpdateAddressThrowsException() {
        Address updatedAddress = Address.builder()
                .street("456 Elm St")
                .city("Shelbyville")
                .state("IL")
                .postalCode("62565")
                .country("USA")
                .build();

        when(addressRepository.findById(address.getAddressId())).thenReturn(empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> addressService.updateAddress(address.getAddressId(), updatedAddress));

        assertEquals("Address not found with ID: " + address.getAddressId(), exception.getMessage());
        verify(addressRepository, times(1)).findById(address.getAddressId());
        verify(addressRepository, times(0)).save(any(Address.class));
    }

    @Test
    void testDeleteAddress() {
        when(addressRepository.existsById(address.getAddressId())).thenReturn(true);

        addressService.deleteAddress(address.getAddressId());

        verify(addressRepository, times(1)).existsById(address.getAddressId());
        verify(addressRepository, times(1)).deleteById(address.getAddressId());
    }

    @Test
    void testDeleteAddressThrowsException() {
        when(addressRepository.existsById(address.getAddressId())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> addressService.deleteAddress(address.getAddressId()));

        assertEquals("Address not found with ID: " + address.getAddressId(), exception.getMessage());
        verify(addressRepository, times(1)).existsById(address.getAddressId());
        verify(addressRepository, times(0)).deleteById(address.getAddressId());
    }
}


