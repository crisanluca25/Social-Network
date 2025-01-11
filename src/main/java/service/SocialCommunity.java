package service;

import domain.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SocialCommunity {
    SocialNetwork socialNetwork;
    Map<Long, List<Long>> adjList = new HashMap<>();

    public SocialCommunity(SocialNetwork socialNetwork) {
        this.socialNetwork = socialNetwork;
    }

    void DFS(Long v, HashMap<Long, Boolean> visited, List<Long> communityMembers) {
        visited.put(v, true);
        communityMembers.add(v);
        System.out.println(v + " " + socialNetwork.findUser(v).getUsername());
        if (adjList.containsKey(v)) {
            adjList.get(v).stream().filter(x -> !visited.containsKey(x)).forEach(x -> DFS(x, visited, communityMembers));
        }
    }

    public int connectedCommunities() {
        adjList = new HashMap<>();
        socialNetwork.getUsers().forEach(user -> {
            List<Long> friends = new ArrayList<>();
            socialNetwork.getFriendships().forEach(friendship -> {
                if (friendship.getIdUser1().equals(user.getId()))
                    friends.add(friendship.getIdUser2());
                if (friendship.getIdUser2().equals(user.getId()))
                    friends.add(friendship.getIdUser1());
            });
            if (!friends.isEmpty())
                this.adjList.put(user.getId(), friends);
        });

        List<Long> ids = new ArrayList<>();
        socialNetwork.getUsers().forEach(user -> {
            ids.add(user.getId());
        });
        AtomicInteger nrOfCommunities = new AtomicInteger();
        HashMap<Long, Boolean> visited = new HashMap<>();
        ids.forEach(v -> {
            if (!visited.containsKey(v)) {
                List<Long> communityMembers = new ArrayList<>();
                DFS(v, visited, communityMembers);
                nrOfCommunities.getAndIncrement();
                System.out.println("Community " + nrOfCommunities + ": " + communityMembers);
            }
        });
        return nrOfCommunities.get();
    }

    public List<User> mostSocialCommunity() {
        final List<User>[] max = new List[]{new ArrayList<>()};

        Map<Long, User> userMap = new HashMap<>();
        socialNetwork.getUsers().forEach(user -> userMap.put(user.getId(), user));

        socialNetwork.getUsers().forEach(user -> {
            List<Long> friends = new ArrayList<>();

            socialNetwork.getFriendships().forEach(friendship -> {
                if (friendship.getIdUser1().equals(user.getId())) {
                    friends.add(friendship.getIdUser2());
                }
                if (friendship.getIdUser2().equals(user.getId())) {
                    friends.add(friendship.getIdUser1());
                }
            });

            if (!friends.isEmpty()) {
                this.adjList.put(user.getId(), friends);
                if (max[0].size() < friends.size() + 1) {
                    max[0] = new ArrayList<>();
                    max[0].add(user);
                    friends.forEach(friendId -> max[0].add(userMap.get(friendId)));
                }
            }
        });
        return max[0];
    }
}
