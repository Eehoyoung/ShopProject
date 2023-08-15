package com.shop.onlyfit.controller;

import com.shop.onlyfit.domain.Item;
import com.shop.onlyfit.domain.SearchItem;
import com.shop.onlyfit.domain.SearchOrder;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.domain.type.OrderStatus;
import com.shop.onlyfit.domain.type.UserGrade;
import com.shop.onlyfit.dto.OrderDto;
import com.shop.onlyfit.dto.OrderPageDto;
import com.shop.onlyfit.dto.item.ItemDto;
import com.shop.onlyfit.dto.item.ItemPageDto;
import com.shop.onlyfit.service.ItemServiceImpl;
import com.shop.onlyfit.service.MarketServiceImpl;
import com.shop.onlyfit.service.UserServiceImpl;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Locale;

@Controller
public class MarketController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final MarketServiceImpl marketService;
    private final UserServiceImpl userService;
    private final ItemServiceImpl itemService;

    @Autowired
    public MarketController(MarketServiceImpl marketService, UserServiceImpl userService, ItemServiceImpl itemService) {
        this.marketService = marketService;
        this.userService = userService;
        this.itemService = itemService;
    }

    @GetMapping("/getMarketId")
    @ResponseBody
    public Long getMarketId(Principal principal) {
        String userName = principal.getName(); // 현재 로그인된 사용자 이름을 얻습니다.
        return marketService.findMarketId(userName);
    }

    @GetMapping("/main/category/{marketId}")
    public String getCategoryPageMarket(@PathVariable Long marketId, @PageableDefault(size = 12) Pageable pageable, Model model) {
        ItemPageDto itemPagingDto = itemService.getItemPagingDtoByCategoryAndMarket(pageable, marketId);
        String marketName = marketService.findMarketNameByMarketId(marketId);
        marketService.updateMarketVisitCount(marketId);
        model.addAttribute("startPage", itemPagingDto.getHomeStartPage());
        model.addAttribute("endPage", itemPagingDto.getHomeEndPage());
        model.addAttribute("itemList", itemPagingDto.getItemPage());
        model.addAttribute("marketName", marketName);
        model.addAttribute("marketId", marketId);

        return "market/market_category";
    }


    @GetMapping("/market/main/{marketId}")
    public String getMemberMainPage(@PathVariable Long marketId, Model model, @PageableDefault(size = 4) Pageable pageable) {
        Page<ItemDto> itemBoards = marketService.findAllItemByMarketId(marketId, pageable);
        Page<OrderDto> orderBoards = marketService.findAllOrderByMarketId(marketId, pageable);
        int allVisitCount = marketService.getVisitCountByMarketId(marketId);
        model.addAttribute("itemList", itemBoards);
        model.addAttribute("orderList", orderBoards);
        model.addAttribute("numVisitors", allVisitCount);
        model.addAttribute("marketId", marketId); // marketId를 모델에 추가합니다.

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
            String newUrl = "/image/Item/" + upperFirstCategory + "/" + secondCategory + "/" + itemName + "/" + originFileName;
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

        if (findUser.getUserGrade() != UserGrade.SELLER) {
            return "redirect:/main/index";
        }

        ItemPageDto itemPageDto = new ItemPageDto();
        if (searchItem.getItem_name() == null) {
            itemPageDto = marketService.findAllItemByPaging(userId, pageable);
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

        return "market/market_item_list";
    }

    @GetMapping("/market/orderList")
    public String getOrderPage(@AuthenticationPrincipal UserDetails userDetails, Model model,
                               @PageableDefault(size = 4) Pageable pageable, SearchOrder searchOrder) {

        String userId = userDetails.getUsername();
        Long marketId = marketService.findMarketId(userId);

        OrderPageDto orderPageDto = new OrderPageDto();

        if (StringUtils.isEmpty(searchOrder.getFirstdate()) && StringUtils.isEmpty(searchOrder.getLastdate()) && StringUtils.isEmpty(searchOrder.getSinput())) {
            orderPageDto = marketService.findAllOrderByPaging(marketId, pageable);
        } else {
            orderPageDto = marketService.findAllOrderByConditionByPaging(marketId, searchOrder, pageable);
        }

        Page<OrderDto> orderBoards = orderPageDto.getOrderBoards();
        int homeStartPage = orderPageDto.getHomeStartPage();
        int homeEndPage = orderPageDto.getHomeEndPage();

        model.addAttribute("orderList", orderBoards);
        model.addAttribute("startPage", homeStartPage);
        model.addAttribute("endPage", homeEndPage);

        model.addAttribute("firstDate", searchOrder.getFirstdate());
        model.addAttribute("lastDate", searchOrder.getLastdate());
        model.addAttribute("oMode", searchOrder.getOmode());
        model.addAttribute("sMode", "buyer");
        model.addAttribute("sInput", searchOrder.getSinput());
        model.addAttribute("oModeStatus", searchOrder.getOmode());

        return "market/market_order";

    }

    @ResponseBody
    @PatchMapping("/market/orderList1/{id}")
    public String orderStatusChangePage(@PathVariable Long id, @RequestParam OrderStatus status) {
        marketService.changeOrderStatus(id, status);

        return "주문 상품 상태 변경완료";
    }
}
