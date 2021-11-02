package akatsuki.restaurantsysteminformation.restauranttable;

import akatsuki.restaurantsysteminformation.enums.TableShape;
import akatsuki.restaurantsysteminformation.enums.TableState;
import akatsuki.restaurantsysteminformation.order.Order;

import javax.persistence.*;

@Entity
@Table(name = "RestaurantTable")
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "state", nullable = false)
    private TableState state;

    @Column(name = "shape", nullable = false)
    private TableShape shape;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private Order activeOrder;

    public RestaurantTable() {
    }

    public RestaurantTable(Long id, String name, TableState state, TableShape shape, boolean isDeleted, Order activeOrder) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.shape = shape;
        this.isDeleted = isDeleted;
        this.activeOrder = activeOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TableState getState() {
        return state;
    }

    public void setState(TableState state) {
        this.state = state;
    }

    public TableShape getShape() {
        return shape;
    }

    public void setShape(TableShape shape) {
        this.shape = shape;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Order getActiveOrder() {
        return activeOrder;
    }

    public void setActiveOrder(Order activeOrder) {
        this.activeOrder = activeOrder;
    }
}
