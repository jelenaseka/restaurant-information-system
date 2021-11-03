package akatsuki.restaurantsysteminformation.unregistereduser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("unregistered-user")
public class UnregisteredUserController {
    private UnregisteredUserServiceImpl unregisteredUserService;

    @Autowired
    public UnregisteredUserController(UnregisteredUserServiceImpl unregisteredUserService) {
        this.unregisteredUserService = unregisteredUserService;
    }

}
