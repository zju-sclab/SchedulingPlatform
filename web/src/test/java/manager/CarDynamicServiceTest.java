package manager;

import com.skywilling.cn.manager.car.model.CarDynamic;
import com.skywilling.cn.manager.car.service.CarDynamicService;
import com.skywilling.cn.web.WebApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {WebApplication.class})
public class CarDynamicServiceTest {
    @Autowired
    CarDynamicService carDynamicService;

    @Test
    public void testGetCarDynamic(){
        CarDynamic carDynamic = carDynamicService.query("00000000112417002");
        System.out.println(carDynamic);
    }
}