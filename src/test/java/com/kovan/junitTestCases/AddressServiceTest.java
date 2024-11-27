package com.kovan.junitTestCases;

import com.kovan.entities.Address;
import com.kovan.entities.User;
import com.kovan.repository.AddressRepository;
import com.kovan.repository.UserRepository;
import com.kovan.service.AddressService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressService addressService;

    private final User user = User.builder().userId(1L).firstName("Abi").build();
    private final Address address = Address.builder()
            .addressId(1L)
            .user(user)
            .state("Karnataka")
            .street("R K Nagar")
            .city("Bangalore")
            .country("India")
            .postalCode("846467")
            .build();

    @Test
    void testAddAddress(){

        when(userRepository.findById(user.getUserId())).thenReturn(of(user));
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        Address result = addressService.addAddress(user.getUserId(),address);

        assertNotNull(result);
        assertEquals(user.getUserId(),result.getUser().getUserId());
        assertEquals(address,result);

    }

    @Test
    void testGetAddressesById(){

        when(addressRepository.findById(address.getAddressId())).thenReturn(of(address));
        Address result = addressService.getAddressesById(address.getAddressId());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(address, result);
        verify(addressRepository,times(1)).findById(address.getAddressId());

    }

    @Test
    void testGetAddressesByUserId() {

        List<Address> addressList = List.of(address);
        when(addressRepository.findByUserUserId(user.getUserId())).thenReturn(addressList);

        List<Address> result = addressService.getAddressesByUserId(user.getUserId());

        assertEquals(1, result.size());
        assertEquals(address, result.getFirst());
        verify(addressRepository,times(1)).findByUserUserId(user.getUserId());
    }

    @Test
    void testUpdateAddress(){
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

        Assertions.assertNotNull(result);
        Assertions.assertEquals(updatedAddress.getStreet(), result.getStreet());
        Assertions.assertEquals(updatedAddress.getCity(), result.getCity());
        verify(addressRepository, times(1)).findById(address.getAddressId());
        verify(addressRepository, times(1)).save(address);
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
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> addressService.deleteAddress(address.getAddressId()));

       verify(addressRepository,times(1)).existsById(address.getAddressId());
    }
}


