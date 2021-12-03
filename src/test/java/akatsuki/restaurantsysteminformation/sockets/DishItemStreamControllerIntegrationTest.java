package akatsuki.restaurantsysteminformation.sockets;

import akatsuki.restaurantsysteminformation.dishitem.DishItem;
import akatsuki.restaurantsysteminformation.dishitem.DishItemService;
import akatsuki.restaurantsysteminformation.dishitem.dto.DishItemActionRequestDTO;
import akatsuki.restaurantsysteminformation.dishitem.dto.DishItemCreateDTO;
import akatsuki.restaurantsysteminformation.enums.ItemState;
import akatsuki.restaurantsysteminformation.order.Order;
import akatsuki.restaurantsysteminformation.order.OrderService;
import akatsuki.restaurantsysteminformation.sockets.dto.SocketResponseDTO;
import akatsuki.restaurantsysteminformation.unregistereduser.UnregisteredUser;
import akatsuki.restaurantsysteminformation.unregistereduser.UnregisteredUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DishItemStreamControllerIntegrationTest {

    @LocalServerPort
    private Integer port;

    private WebSocketStompClient webSocketStompClient;

    @Autowired
    private DishItemService dishItemService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UnregisteredUserService unregisteredUserService;

    @BeforeEach
    public void setup() {
        webSocketStompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
    }

    @Test
    public void create_Valid_SavedObject() throws Exception {

        int size = dishItemService.getAll().size();

        BlockingQueue<SocketResponseDTO> blockingQueue = new ArrayBlockingQueue(1);
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
                .connect(String.format("ws://localhost:%d/app/stomp", port), new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);

        session.subscribe("/topic/dish-item", new MyStompFrameHandler(blockingQueue));
        DishItemCreateDTO dto = new DishItemCreateDTO(4L, 2, null, 2L);

        session.send("/app/dish-item/create", dto);
        Thread.sleep(100);

        SocketResponseDTO returnDTO = blockingQueue.poll(1, SECONDS);

        List<DishItem> dishItems = dishItemService.getAll();

        assertNotNull(returnDTO);
        assertEquals(size + 1, dishItems.size());
        assertTrue(returnDTO.isSuccessfullyFinished());

        dishItemService.deleteById(dishItems.get(dishItems.size() - 1).getId());
    }

    @Test
    public void create_InvalidOrderId_ExceptionThrown() throws Exception {

        int size = dishItemService.getAll().size();

        BlockingQueue<SocketResponseDTO> blockingQueue = new ArrayBlockingQueue(1);
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
                .connect(String.format("ws://localhost:%d/app/stomp", port), new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);

        session.subscribe("/topic/dish-item", new MyStompFrameHandler(blockingQueue));
        DishItemCreateDTO dto = new DishItemCreateDTO(4L, 2, null, 44L);

        session.send("/app/dish-item/create", dto);
        Thread.sleep(100);

        SocketResponseDTO returnDTO = blockingQueue.poll(1, SECONDS);

        List<DishItem> dishItems = dishItemService.getAll();

        assertNotNull(returnDTO);
        assertEquals(size, dishItems.size());
        assertEquals("Order with the id 44 is not found in the database.", returnDTO.getMessage());
        assertFalse(returnDTO.isSuccessfullyFinished());
    }

    @Test
    public void create_InvalidItemId_ExceptionThrown() throws Exception {

        int size = dishItemService.getAll().size();

        BlockingQueue<SocketResponseDTO> blockingQueue = new ArrayBlockingQueue(1);
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
                .connect(String.format("ws://localhost:%d/app/stomp", port), new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);

        session.subscribe("/topic/dish-item", new MyStompFrameHandler(blockingQueue));
        DishItemCreateDTO dto = new DishItemCreateDTO(1L, 2, null, 2L);

        session.send("/app/dish-item/create", dto);
        Thread.sleep(100);

        SocketResponseDTO returnDTO = blockingQueue.poll(1, SECONDS);

        List<DishItem> dishItems = dishItemService.getAll();

        assertNotNull(returnDTO);
        assertEquals(size, dishItems.size());
        assertEquals("Item type is not DISH.", returnDTO.getMessage());
        assertFalse(returnDTO.isSuccessfullyFinished());
    }

    @Test
    public void update_Valid_UpdatedObject() throws Exception {

        BlockingQueue<SocketResponseDTO> blockingQueue = new ArrayBlockingQueue(1);
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
                .connect(String.format("ws://localhost:%d/app/stomp", port), new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);

        session.subscribe("/topic/dish-item", new MyStompFrameHandler(blockingQueue));
        DishItemCreateDTO dto = new DishItemCreateDTO(4L, 5, null, 1L);

        session.send("/app/dish-item/update/1", dto);
        Thread.sleep(100);

        SocketResponseDTO returnDTO = blockingQueue.poll(1, SECONDS);

        assertNotNull(returnDTO);
        assertTrue(returnDTO.isSuccessfullyFinished());
        assertEquals("Dish item with 1 is successfully updated!", returnDTO.getMessage());

        dto = new DishItemCreateDTO(4L, 1, null, 1L);
        dishItemService.update(dto, 1);

        Order order = orderService.getOneWithAll(1L);
        order.setTotalPrice(8);
        orderService.save(order);
    }

    @Test
    public void update_OrderNotContainingItem_ExceptionThrown() throws Exception {

        BlockingQueue<SocketResponseDTO> blockingQueue = new ArrayBlockingQueue(1);
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
                .connect(String.format("ws://localhost:%d/app/stomp", port), new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);

        session.subscribe("/topic/dish-item", new MyStompFrameHandler(blockingQueue));
        DishItemCreateDTO dto = new DishItemCreateDTO(4L, 5, null, 1L);

        session.send("/app/dish-item/update/5", dto);
        Thread.sleep(100);

        SocketResponseDTO returnDTO = blockingQueue.poll(1, SECONDS);

        assertNotNull(returnDTO);
        assertFalse(returnDTO.isSuccessfullyFinished());
        assertEquals("Dish item order id is not equal to 1. Order cannot be changed.", returnDTO.getMessage());
    }

    @Test
    public void update_InvalidType_ExceptionThrown() throws Exception {

        BlockingQueue<SocketResponseDTO> blockingQueue = new ArrayBlockingQueue(1);
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
                .connect(String.format("ws://localhost:%d/app/stomp", port), new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);

        session.subscribe("/topic/dish-item", new MyStompFrameHandler(blockingQueue));
        DishItemCreateDTO dto = new DishItemCreateDTO(2L, 5, null, 1L);

        session.send("/app/dish-item/update/1", dto);
        Thread.sleep(100);

        SocketResponseDTO returnDTO = blockingQueue.poll(1, SECONDS);

        assertNotNull(returnDTO);
        assertFalse(returnDTO.isSuccessfullyFinished());
        assertEquals("Item type is not DISH.", returnDTO.getMessage());
    }

    @Test
    public void update_InvalidDishState_ExceptionThrown() throws Exception {

        BlockingQueue<SocketResponseDTO> blockingQueue = new ArrayBlockingQueue(1);
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
                .connect(String.format("ws://localhost:%d/app/stomp", port), new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);

        session.subscribe("/topic/dish-item", new MyStompFrameHandler(blockingQueue));
        DishItemCreateDTO dto = new DishItemCreateDTO(4L, 5, null, 1L);

        session.send("/app/dish-item/update/2", dto);
        Thread.sleep(100);

        SocketResponseDTO returnDTO = blockingQueue.poll(1, SECONDS);

        assertNotNull(returnDTO);
        assertFalse(returnDTO.isSuccessfullyFinished());
        assertEquals("Cannot change dish item, because its state is preparation.", returnDTO.getMessage());
    }

    @Test
    public void update_InvalidItemId_ExceptionThrown() throws Exception {

        BlockingQueue<SocketResponseDTO> blockingQueue = new ArrayBlockingQueue(1);
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
                .connect(String.format("ws://localhost:%d/app/stomp", port), new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);

        session.subscribe("/topic/dish-item", new MyStompFrameHandler(blockingQueue));
        DishItemCreateDTO dto = new DishItemCreateDTO(44L, 5, null, 1L);

        session.send("/app/dish-item/update/1", dto);
        Thread.sleep(100);

        SocketResponseDTO returnDTO = blockingQueue.poll(1, SECONDS);

        assertNotNull(returnDTO);
        assertFalse(returnDTO.isSuccessfullyFinished());
        assertEquals("Item with the id 44 is not found in the database.", returnDTO.getMessage());
    }

    @Test
    public void update_InvalidOrderId_ExceptionThrown() throws Exception {

        BlockingQueue<SocketResponseDTO> blockingQueue = new ArrayBlockingQueue(1);
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
                .connect(String.format("ws://localhost:%d/app/stomp", port), new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);

        session.subscribe("/topic/dish-item", new MyStompFrameHandler(blockingQueue));
        DishItemCreateDTO dto = new DishItemCreateDTO(4L, 5, null, 100L);

        session.send("/app/dish-item/update/1", dto);
        Thread.sleep(100);

        SocketResponseDTO returnDTO = blockingQueue.poll(1, SECONDS);

        assertNotNull(returnDTO);
        assertFalse(returnDTO.isSuccessfullyFinished());
        assertEquals("Order with the id 100 is not found in the database.", returnDTO.getMessage());
    }

    @Test
    public void changeStateOfDishItem_OnHoldToPreparation_SavedObject() throws Exception {

        BlockingQueue<SocketResponseDTO> blockingQueue = new ArrayBlockingQueue(1);
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
                .connect(String.format("ws://localhost:%d/app/stomp", port), new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);

        session.subscribe("/topic/dish-item", new MyStompFrameHandler(blockingQueue));
        DishItemActionRequestDTO dto = new DishItemActionRequestDTO(3L, 1L);

        session.send("/app/dish-item/change-state", dto);
        Thread.sleep(100);

        SocketResponseDTO returnDTO = blockingQueue.poll(1, SECONDS);

        assertNotNull(returnDTO);
        assertTrue(returnDTO.isSuccessfullyFinished());

        DishItem dishItem = dishItemService.findOneActiveAndFetchItemAndChef(1L);
        dishItem.setState(ItemState.ON_HOLD);
        dishItem.setChef(null);
        dishItemService.save(dishItem);
    }

    @Test
    public void changeStateOfDishItem_PreparationToReady_SavedObject() throws Exception {

        BlockingQueue<SocketResponseDTO> blockingQueue = new ArrayBlockingQueue(1);
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
                .connect(String.format("ws://localhost:%d/app/stomp", port), new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);

        session.subscribe("/topic/dish-item", new MyStompFrameHandler(blockingQueue));
        DishItemActionRequestDTO dto = new DishItemActionRequestDTO(3L, 2L);

        session.send("/app/dish-item/change-state", dto);
        Thread.sleep(100);

        SocketResponseDTO returnDTO = blockingQueue.poll(1, SECONDS);

        assertNotNull(returnDTO);
        assertTrue(returnDTO.isSuccessfullyFinished());

        DishItem dishItem = dishItemService.findOneActiveAndFetchItemAndChef(2L);
        dishItem.setState(ItemState.PREPARATION);
        dishItemService.save(dishItem);
    }
}
