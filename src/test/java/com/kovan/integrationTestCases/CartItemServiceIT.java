package com.kovan.integrationTestCases;

import com.kovan.entities.*;
import com.kovan.repository.*;
import com.kovan.service.CartItemService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CartItemServiceIT {

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private ShoppingCart testCart;
    private Book testBook;

    @BeforeEach
    public void setup() {
        cartItemRepository.deleteAll();
        shoppingCartRepository.deleteAll();
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        User user = User.builder()
                .firstName("Test User")
                .email("Test@gmail.com")
                .build();
        userRepository.save(user);
        testCart = ShoppingCart.builder().user(user).build();
        shoppingCartRepository.save(testCart);

        Category testCategory = Category.builder().name("Fiction").build();
        categoryRepository.save(testCategory);
        testBook = Book.builder()
                .title("Test Book")
                .author("Test Author")
                .price(BigDecimal.valueOf(19.99))
                .isbn("1234567890123")
                .publicationYear(2023)
                .stockQuantity(10)
                .category(testCategory)
                .build();
        bookRepository.save(testBook);
    }

    @Test
    void addCartItemTest() {

        CartItem cartItem = cartItemService.addCartItem(testCart.getCartId(), testBook.getBookId(), 2);

        assertThat(cartItem).isNotNull();
        assertThat(cartItem.getCartItemId()).isNotNull();
        assertThat(cartItem.getQuantity()).isEqualTo(2);
        assertThat(cartItem.getBook().getBookId()).isEqualTo(testBook.getBookId());
        assertThat(cartItem.getCart().getCartItems()).isEqualTo(testCart.getCartItems());
    }

    @Test
    void updateCartItemTest() {

        CartItem cartItem = cartItemService.addCartItem(testCart.getCartId(), testBook.getBookId(), 2);

        cartItemService.updateCartItem(cartItem.getCartItemId(), 5);

        Optional<CartItem> updatedCartItem = cartItemRepository.findById(cartItem.getCartItemId());

        assertThat(updatedCartItem).isPresent();
        assertThat(updatedCartItem.get().getQuantity()).isEqualTo(5);
    }

    @Test
    void deleteCartItemTest() {

        CartItem cartItem = cartItemService.addCartItem(testCart.getCartId(), testBook.getBookId(), 2);

        cartItemService.deleteCartItem(cartItem.getCartItemId());

        Optional<CartItem> deletedCartItem = cartItemRepository.findById(cartItem.getCartItemId());
        assertThat(deletedCartItem).isNotPresent();
    }

    @Test
    void getAllCartItemsTest() {

        Category testCategory = Category.builder().name("Fiction").build();
        categoryRepository.save(testCategory);

        cartItemService.addCartItem(testCart.getCartId(), testBook.getBookId(), 1);

        Book anotherBook = Book.builder()
                .title("Another Book")
                .author("Another Author")
                .price(BigDecimal.valueOf(25.99))
                .isbn("9876543210987")
                .publicationYear(2022)
                .stockQuantity(5)
                .category(testCategory)
                .build();
        bookRepository.save(anotherBook);
        cartItemService.addCartItem(testCart.getCartId(), anotherBook.getBookId(), 3);

        List<CartItem> cartItems = cartItemService.getAllCartItems();

        assertThat(cartItems).hasSize(2);
        assertThat(cartItems).extracting(CartItem::getQuantity).contains(1, 3);
        assertThat(cartItems).extracting(cartItem -> cartItem.getBook().getTitle())
                .contains("Test Book", "Another Book");
    }
}

