package stop.covid.challenge.cafein.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stop.covid.challenge.cafein.domain.model.PersonalCafe;

@Repository
public interface PersonalCafeRepository extends JpaRepository<PersonalCafe, Long> {

    PersonalCafe findPersonalCafeByNickname(String nickname);

}
