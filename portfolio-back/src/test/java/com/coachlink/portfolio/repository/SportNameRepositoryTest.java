package com.coachlink.portfolio.repository;

import com.coachlink.portfolio.entity.SportName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import java.util.Optional;

@SpringBootTest
public class SportNameRepositoryTest {

    @Autowired
    private SportNameRepository sportNameRepository;

    @Test
    public void insertSportName() {
        sportNameRepository.save(SportName.builder()
                .mainName("Test Main")
                .subName("Test Sub")
                .build());
    }

    @Test
    public void selectSportNameBySportId() {
        Optional<SportName> result = sportNameRepository.findById(5L);
        System.out.println(result.toString());
    }

    @Test
    public void selectAllSportName(){
        sportNameRepository.findAll(Sort.by("mainName")).forEach(sportName -> {
            System.out.println(sportName.toString());
        });

    }

    @Test
    public void updateBySportId() {
        Optional<SportName> result = sportNameRepository.findById(52L);
        if(result.isPresent()) {
            SportName tmpSportName = result.get();
            tmpSportName.updateMainName("Main update test");
            tmpSportName.updateSubName("Sub update test");

            sportNameRepository.save(tmpSportName);
        }
    }

    @Test
    public void deleteBySportId() {
        Optional<SportName> result = sportNameRepository.findById(52L);
        if(result.isPresent()) {
            sportNameRepository.deleteById(52L);

        }
    }

}
