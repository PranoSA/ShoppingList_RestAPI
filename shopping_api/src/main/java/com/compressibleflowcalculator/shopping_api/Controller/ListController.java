package com.compressibleflowcalculator.shopping_api.Controller;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.compressibleflowcalculator.shopping_api.Controller.Requests.ItemPostRequest;
import com.compressibleflowcalculator.shopping_api.Controller.Requests.ListRequest;
import com.compressibleflowcalculator.shopping_api.Controller.Responses.Group.InviteResponse;
import com.compressibleflowcalculator.shopping_api.Entity.Group;
import com.compressibleflowcalculator.shopping_api.Entity.Group_User;
import com.compressibleflowcalculator.shopping_api.Entity.Invitation;
import com.compressibleflowcalculator.shopping_api.Entity.Item;
import com.compressibleflowcalculator.shopping_api.Entity.ShoppingList;
import com.compressibleflowcalculator.shopping_api.Repository.GroupRepository;
import com.compressibleflowcalculator.shopping_api.Repository.GroupUserRepository;
import com.compressibleflowcalculator.shopping_api.Repository.InviteRepository;
import com.compressibleflowcalculator.shopping_api.Service.GroupService;
import com.compressibleflowcalculator.shopping_api.Service.InviteService;
import com.compressibleflowcalculator.shopping_api.Service.ListService;
import com.compressibleflowcalculator.shopping_api.Views.Item_View;
import com.compressibleflowcalculator.shopping_api.Views.List_View;
import com.fasterxml.jackson.annotation.JsonView;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@CrossOrigin(maxAge = 7200, allowedHeaders = "*", origins = {
        "http://localhost:3000" }, methods = {
                RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.POST })
public class ListController {
    @Autowired
    private ListService listservice;

    @RequestMapping(value = "/group/{groupid}/list", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ShoppingList AddList(@AuthenticationPrincipal Jwt jwt, @PathVariable String groupid,
            @JsonView(List_View.ListPostView.class) @RequestBody ShoppingList req) {

        String idk = jwt.getClaim("sub");

        listservice.getListsByGroup(UUID.fromString(idk), UUID.fromString(groupid));

        ShoppingList newlist = listservice.newList(UUID.fromString(idk), UUID.fromString(groupid), req.getDescription(),
                req.getDate());

        return newlist;

    }

    @RequestMapping(value = "/group/{groupid}/list", method = RequestMethod.GET, produces = "application/json")
    public List<ShoppingList> GetList(@AuthenticationPrincipal Jwt jwt, @PathVariable String groupid) {

        String idk = jwt.getClaim("sub");

        List<ShoppingList> lists = listservice.getListsByGroup(UUID.fromString(idk), UUID.fromString(groupid));

        return lists;
    }

    @RequestMapping(value = "/group/{groupid}/list/{listid}/items", method = RequestMethod.GET, produces = "application/json")
    public List<Item> GetItems(@AuthenticationPrincipal Jwt jwt, @PathVariable String groupid,
            @PathVariable String listid) {

        String userid = jwt.getClaim("sub");

        List<Item> items = listservice.getListItems(UUID.fromString(listid), UUID.fromString(userid));

        return items;
    }

    @RequestMapping(value = "/group/{groupid}/list/{listid}/item/{itemid}", method = RequestMethod.DELETE, produces = "application/json")
    public Item DeleteItem(@AuthenticationPrincipal Jwt jwt, @PathVariable String listid, @PathVariable String itemid) {

        UUID userid = UUID.fromString(jwt.getClaim("sub"));

        Item response = listservice.deleteItemFromList(UUID.fromString(listid), userid, UUID.fromString(itemid));

        return response;
    }

    @RequestMapping(value = "/group/{groupid}/list/{listid}/items", method = RequestMethod.POST, produces = "application/json")
    public Item CreateItem(@AuthenticationPrincipal Jwt jwt, @PathVariable String listid,
            @RequestBody ItemPostRequest body) {

        UUID userid = UUID.fromString(jwt.getClaim("sub"));

        Item response = listservice.addItemToList(UUID.fromString(listid), userid, body.getQuantity(), body.getPrice(),
                body.getName());

        return response;
    }

}
