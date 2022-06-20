package stop.covid.challenge.cafein.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import stop.covid.challenge.cafein.domain.model.HashTag;
import stop.covid.challenge.cafein.domain.model.Image;
import stop.covid.challenge.cafein.domain.model.Menu;
import stop.covid.challenge.cafein.domain.model.PersonalCafe;
import stop.covid.challenge.cafein.dto.MenuDto;
import stop.covid.challenge.cafein.repository.HashTagRepository;
import stop.covid.challenge.cafein.repository.ImageRepository;
import stop.covid.challenge.cafein.repository.MenuRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final ImageRepository imageRepository;
    private final HashTagRepository hashTagRepository;
    private final ImageService imageService;

    @Transactional
    public Menu getMenu(Long id) {
        return menuRepository.findById(id).get();
    }

    @Transactional
    public List<Menu> getAllMenu(PersonalCafe personalCafe) {
        return menuRepository.findAllByPersonalCafe(personalCafe);
    }

    @Transactional
    public Long save(List<MultipartFile> files, MenuDto menuDto) {
        Menu menu = menuRepository.save(Menu.builder()
            .title(menuDto.getTitle())
            .writing(menuDto.getWriting())
            .build());

        checkImageAndHashTag(files, menuDto, menu);
        return menu.getId();
    }

    @Transactional
    public Boolean update(Long id, List<MultipartFile> files, MenuDto menuDto) {
        Optional<Menu> oMenu = menuRepository.findById(id);

        if (oMenu.isEmpty())
            return false;

        Menu menu = oMenu.get();
        if (menuDto.getTitle().length() > 0) menu.setTitle(menuDto.getTitle());
        if (menuDto.getWriting().length() > 0) menu.setWriting(menu.getWriting());
        Menu savedMenu = menuRepository.save(menu);
        checkImageAndHashTag(files, menuDto, savedMenu);
        return true;
    }

    @Transactional
    public void delete(Long id) {
        Menu menu = menuRepository.findById(id).get();
        List<Image> images = menu.getImages();
        for (Image image : images) {
            imageService.deleteFile(image);
        }
        menuRepository.delete(menu);
    }

    private void checkImageAndHashTag(List<MultipartFile> files, MenuDto menuDto, Menu menu) {
        if (files.toArray().length > 0) {
            List<Image> images = imageService.uploadFile(files);
            for (Image image : images) {
                image.setMenu(menu);
            }
            List<Image> imageList = imageRepository.saveAll(images);
            menu.addMenuImage(imageList);
            menuRepository.save(menu);
        }
        if (menuDto.getHashTags().toArray().length > 0) {
            List<HashTag> hashTags = new ArrayList<>();
            for (HashTag hashTag : menuDto.getHashTags()) {
                hashTag.setMenu(menu);
                hashTags.add(hashTag);
            }
            hashTagRepository.saveAll(hashTags);
        }
    }

}
