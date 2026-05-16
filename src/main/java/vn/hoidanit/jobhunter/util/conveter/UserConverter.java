package vn.hoidanit.jobhunter.util.conveter;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;

public class UserConverter {
    public ResUserDTO toDTO(User user){
        ResUserDTO resUserDTO = new ResUserDTO();
        resUserDTO.setId(user.getId());
        resUserDTO.setName(user.getName());
        resUserDTO.setEmail(user.getEmail());
        resUserDTO.setAddress(user.getAddress());
        resUserDTO.setAge(user.getAge());
        resUserDTO.setGender(user.getGender());
        resUserDTO.setCreatedAt(user.getCreatedAt());
        resUserDTO.setUpdatedAt(user.getUpdatedAt());
        return resUserDTO;
    }
}
