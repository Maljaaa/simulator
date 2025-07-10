package sm.playground.simulator;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import sm.playground.simulator.kisapi.token.service.StockApiTestService;

@SpringBootApplication
@EnableScheduling
public class SimulatorApplication {

//	private final StockApiTestService stockApiTestService;
//
//    public SimulatorApplication(StockApiTestService stockApiTestService) {
//        this.stockApiTestService = stockApiTestService;
//    }

    public static void main(String[] args) {
		SpringApplication.run(SimulatorApplication.class, args);
	}

//	@Override
//	public void run(String... args) throws Exception {
//		stockApiTestService.callPriceApi();
//	}
}
