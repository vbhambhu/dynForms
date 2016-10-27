package kennedy.ox.ac.uk.Controllers;


import kennedy.ox.ac.uk.Models.Group;
import kennedy.ox.ac.uk.Models.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

@Controller
public class GroupController {

    @Autowired
    MongoOperations mongoOperation;

    @RequestMapping(value="/groups", method= RequestMethod.GET)
    public String listGroups(Model model) {

        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(false));
        List<Group> groups =  mongoOperation.find(query, Group.class);
        model.addAttribute("groups", groups);

        return "groups/list";
    }


    @RequestMapping(value="/groups/new", method= RequestMethod.GET)
    public String addGroups(Group group) {
        return "groups/create";
    }


    @RequestMapping(value="/groups/new", method= RequestMethod.POST)
    public String addGroups(@Valid Group group, BindingResult bindingResult) {

        //Check if uniqe
        String identifier = group.getName().trim().toUpperCase().replaceAll("[\\s\\-()]", "_");

        if(isGroupExists(identifier, null)){
            bindingResult.rejectValue("name","group.name", "Group name already exists.");
        }

        if (bindingResult.hasErrors()) {
            return "groups/create";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        group.setOwner(auth.getName());
        group.setIdentifier(identifier);
        mongoOperation.save(group);

        return "redirect:/groups/"+group.getId();
    }


    @RequestMapping(value="/groups/{id}", method= RequestMethod.GET)
    public String details(Model model, @PathVariable String id) {

        // Check if right permission to edit - TODO
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(false).and("_id").is(new ObjectId(id)));
        Group group =  mongoOperation.findOne(query, Group.class);
        model.addAttribute("group", group);


        query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(false));
        List<User> users =  mongoOperation.find(query, User.class);
        model.addAttribute("users", users);

        return "groups/details";
    }


    @RequestMapping(value="/groups/{id}", method= RequestMethod.POST)
    public String update(@Valid Group group, BindingResult bindingResult, Model model, @PathVariable String id) {

        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(false));
        List<User> users =  mongoOperation.find(query, User.class);
        model.addAttribute("users", users);

        String identifier = group.getName().trim().toUpperCase().replaceAll("[\\s\\-()]", "_");

        if(isGroupExists(identifier, group.getId())){
            bindingResult.rejectValue("name","group.name", "Group name already exists.");
        }

        if (!bindingResult.hasErrors()) {
            group.setIdentifier(identifier);
            mongoOperation.save(group);
            model.addAttribute("succ_msg", "Group updated successfully!");
        }

        return "groups/details";
    }


    @RequestMapping(value="/groups/members/{gid}", method = { RequestMethod.GET, RequestMethod.POST } )
    public String members(Model model, @PathVariable String gid, @RequestParam(value = "newMember", required = false) String newMember) {

        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(false).and("_id").is(new ObjectId(gid)));
        Group group =  mongoOperation.findOne(query, Group.class);
        model.addAttribute("group", group);

        query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(false));
        List<User> users =  mongoOperation.find(query, User.class);
        model.addAttribute("users", users);


        if(newMember != null){

            if(isAlreadyMember(newMember, group.getIdentifier())){

                model.addAttribute("error", "User is already in this group.");

                //error
            } else{
                query = new Query();
                query.addCriteria(Criteria.where("_id").is(new ObjectId(newMember)));

                Update update = new Update();
                update.addToSet("groups",group.getIdentifier());
                mongoOperation.findAndModify(query,update,User.class);

                //increment member count
                group.setMembersCount(group.getMembersCount() + 1);
                mongoOperation.save(group);

            }

        }


        query = new Query();
        query.addCriteria(Criteria.where("groups").in(group.getIdentifier()));
        List<User> members =  mongoOperation.find(query, User.class);
        model.addAttribute("members", members);



        return "groups/members";

    }


    @RequestMapping(value="/groups/memebers/delete/{uid}/{grpIdentifier}", method= RequestMethod.POST)
    public String deleteMember(Model model, @PathVariable String uid,  @PathVariable String grpIdentifier) {

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(uid)));

        Update update = new Update();
        update.pull("groups",grpIdentifier);
        mongoOperation.findAndModify(query,update,User.class);

        return "redirect:/groups/memebers/"+uid;

    }


    private Boolean isAlreadyMember(String userId, String groupIdentifier){

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(userId)).and("groups").in(groupIdentifier));
        User user = mongoOperation.findOne(query, User.class);
        return (user == null) ? false : true;
    }


    private Boolean isGroupExists(String groupName, String groupId){
        Query query = new Query();

        if(groupId == null){
            query.addCriteria(Criteria.where("identifier").is(groupName));
        } else{
            query.addCriteria(Criteria.where("identifier").is(groupName).and("_id").ne(new ObjectId(groupId)));
        }

        Group group = mongoOperation.findOne(query, Group.class);
        return (group == null) ? false : true;
    }








}
