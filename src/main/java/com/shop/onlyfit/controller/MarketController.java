package com.shop.onlyfit.controller;

import com.shop.onlyfit.domain.Item;
import com.shop.onlyfit.domain.SearchItem;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.domain.type.UserGrade;
import com.shop.onlyfit.dto.OrderDto;
import com.shop.onlyfit.dto.item.ItemDto;
import com.shop.onlyfit.dto.item.ItemPageDto;
import com.shop.onlyfit.service.MarketServiceImpl;
import com.shop.onlyfit.service.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Controller
public class MarketController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final MarketServiceImpl marketService;
    private final UserServiceImpl userService;

    @Autowired
    public MarketController(MarketServiceImpl marketService, UserServiceImpl userService) {
        this.marketService = marketService;
        this.userService = userService;
    }

    @GetMapping("/market/main")
    public String getMemberMainPage(Model model, @PageableDefault(size = 4) Pageable pageable) {
        Page<User> memberBoards = marketService.findAllMemberByOrderByCreatedAt(pageable);
        Page<ItemDto> itemBoards = marketService.findAllItem(pageable);
        Page<OrderDto> orderBoards = marketService.findAllOrder(pageable);
        int allVisitCount = marketService.getVisitCount();

        model.addAttribute("memberList", memberBoards);
        model.addAttribute("itemList", itemBoards);
        model.addAttribute("orderList", orderBoards);
        model.addAttribute("numVisitors", allVisitCount);

        return "market/market_main";
    }

    @GetMapping("/market/register")
    public String getRegisterItemPage() {
        return "market/market_register_item";
    }

    @PostMapping("/market/register")
    public String requestupload2(@AuthenticationPrincipal UserDetails userDetails, MultipartHttpServletRequest mtfRequest, @RequestParam("cmode1") String firstCategory
            , @RequestParam("cmode2") String secondCategory
            , @RequestParam("cmode3") String thirdCategory
            , @RequestParam("item_name") String itemName
            , @RequestParam("price") int itemPrice
            , @RequestParam("salestatus") String saleStatus
            , @RequestParam("info") String itemInfo
            , @RequestParam("color") String itemColor
            , @RequestParam("size") String itemSize
            , @RequestParam("stock_quantity") int itemQuantity
            , @RequestParam("fabric") String itemFabric
            , @RequestParam("model") String itemModel
    ) {

        String folderPath = "C:\\Users\\LeeHoYoung\\IdeaProjects\\onlyfit\\src\\main\\resources\\static\\image\\item" + firstCategory + "\\" + secondCategory + "\\" + itemName + "\\";

        File newFile = new File(folderPath);
        if (newFile.mkdirs()) {
            logger.info("directory make ok");
        } else {
            logger.warn("directory can't make");
        }

        List<MultipartFile> fileList = mtfRequest.getFiles("upload_image");
        Long newItemIdx = marketService.getMaxItemIdx();

        for (int i = 0; i < fileList.size(); i++) {
            String originFileName = fileList.get(i).getOriginalFilename();
            String safeFile = folderPath + originFileName;

            String upperFirstCategory = firstCategory.toUpperCase(Locale.ROOT);
            String newUrl = "/image/Item/" + upperFirstCategory + "/" +  secondCategory  + "/" + itemName + "/" + originFileName;
            User findMarket = userService.findByLoginId(userDetails.getUsername());
            Item item = new Item(firstCategory, secondCategory, thirdCategory, itemName, itemPrice, itemInfo, itemColor, itemFabric, itemModel, itemSize, itemQuantity, newUrl, saleStatus, newItemIdx + 1, true, findMarket.getMarket());

            if (i == 0) {
                item.setRep(true);
            } else {
                item.setRep(false);
            }
            marketService.saveItem(item);
            try {
                fileList.get(i).transferTo(new File(safeFile));
            } catch (IllegalStateException | IOException e) {
                e.printStackTrace();
            }
        }
        return "redirect:/market/itemList";
    }

    @GetMapping("/market/itemList")
    public String itemListPage(@AuthenticationPrincipal UserDetails userDetails, Model model,
                               @PageableDefault(size = 5) Pageable pageable, SearchItem searchItem) {
        String userId = userDetails.getUsername();
        User findUser = userService.findUserByLoginId(userId);

        if(findUser.getUserGrade() != UserGrade.SELLER){
            return "redirect:/main/index";
        }

        ItemPageDto itemPageDto = new ItemPageDto();
        if (searchItem.getItem_name() == null) {
            itemPageDto = marketService.findAllItemByPaging(userId,pageable);
        } else {
            itemPageDto = marketService.findAllItemByConditionByPaging(userId, searchItem, pageable);
        }
        Page<ItemDto> itemBoards = itemPageDto.getItemPage();
        int homeStartPage = itemPageDto.getHomeStartPage();
        int homeEndPage = itemPageDto.getHomeEndPage();

        model.addAttribute("productList", itemBoards);
        model.addAttribute("startPage", homeStartPage);
        model.addAttribute("endPage", homeEndPage);

        model.addAttribute("saleStatus", searchItem.getSalestatus());
        model.addAttribute("firstCategory", searchItem.getCmode());
        model.addAttribute("itemName", searchItem.getItem_name());

        return "market/admin_item_list";
    }
}
