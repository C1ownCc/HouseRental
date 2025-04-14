package com.house.rental.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.house.rental.common.Result;
import com.house.rental.entity.Booking;
import com.house.rental.entity.User;
import com.house.rental.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/booking")
public class BookingController {
    
    @Autowired
    private BookingService bookingService;
    
    @PostMapping
    public Result<String> createBooking(@RequestBody Booking booking, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return Result.error("未登录");
            }
            
            booking.setUserId(user.getId());
            bookingService.createBooking(booking);
            return Result.success("预约成功");
        } catch (Exception e) {
            return Result.error("预约失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/list")
    public Result<Map<String, Object>> getBookingList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String houseTitle,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return Result.error("未登录");
            }
            
            IPage<Booking> pageResult;
            if ("admin".equals(user.getRole())) {
                // 管理员可以查看所有预约
                pageResult = bookingService.getAllBookingList(page, size, username, houseTitle, status, startDate, endDate);
            } else {
                // 普通用户只能查看自己的预约
                pageResult = bookingService.getUserBookingList(user.getId(), page, size);
            }
            
            return Result.success(Map.of(
                    "list", pageResult.getRecords(),
                    "total", pageResult.getTotal()
            ));
        } catch (Exception e) {
            return Result.error("获取预约列表失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/landlord/list")
    public Result<Map<String, Object>> getLandlordBookingList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return Result.error("未登录");
            }
            
            IPage<Booking> pageResult = bookingService.getLandlordBookingList(user.getId(), page, size);
            return Result.success(Map.of(
                    "list", pageResult.getRecords(),
                    "total", pageResult.getTotal()
            ));
        } catch (Exception e) {
            return Result.error("获取预约列表失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/cancel")
    public Result<String> cancelBooking(@PathVariable Long id, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return Result.error("未登录");
            }
            
            // 管理员可以取消任何预约，普通用户只能取消自己的预约
            if ("admin".equals(user.getRole())) {
                bookingService.updateBookingStatus(id, 2);  // 2 表示已取消
            } else {
                bookingService.cancelBooking(id, user.getId());
            }
            return Result.success("取消成功");
        } catch (Exception e) {
            return Result.error("取消失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/reject")
    public Result<String> rejectBooking(@PathVariable Long id, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return Result.error("未登录");
            }
            
            bookingService.rejectBooking(id, user.getId());
            return Result.success("拒绝成功");
        } catch (Exception e) {
            return Result.error("拒绝失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/confirm")
    public Result<String> confirmBooking(@PathVariable Long id, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return Result.error("未登录");
            }
            
            // 管理员可以确认任何预约，房东只能确认自己房源的预约
            if ("admin".equals(user.getRole())) {
                bookingService.updateBookingStatus(id, 1);  // 1 表示已确认
            } else {
                bookingService.confirmBooking(id, user.getId());
            }
            return Result.success("确认成功");
        } catch (Exception e) {
            return Result.error("确认失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/complete")
    public Result<String> completeBooking(@PathVariable Long id, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return Result.error("未登录");
            }
            
            // 管理员可以完成任何预约，房东只能完成自己房源的预约
            if ("admin".equals(user.getRole())) {
                bookingService.updateBookingStatus(id, 3);  // 3 表示已完成
            } else {
                bookingService.completeBooking(id, user.getId());
            }
            return Result.success("完成成功");
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<String> deleteBooking(@PathVariable Long id, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return Result.error("未登录");
            }
            
            // 管理员可以删除任何预约，其他用户只能删除自己相关的预约
            if ("admin".equals(user.getRole())) {
                bookingService.deleteBooking(id, null);  // 管理员删除不需要检查用户ID
            } else {
                bookingService.deleteBooking(id, user.getId());
            }
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/batch/confirm")
    public Result<String> batchConfirmBooking(@RequestBody Map<String, List<Long>> body, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return Result.error("未登录");
            }
            
            List<Long> ids = body.get("ids");
            if (ids == null || ids.isEmpty()) {
                return Result.error("请选择要确认的预约");
            }
            
            // 管理员可以批量确认任何预约，房东只能确认自己房源的预约
            if ("admin".equals(user.getRole())) {
                for (Long id : ids) {
                    bookingService.updateBookingStatus(id, 1);  // 1 表示已确认
                }
            } else {
                bookingService.batchConfirmBooking(ids, user.getId());
            }
            return Result.success("批量确认成功");
        } catch (Exception e) {
            return Result.error("批量确认失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/batch/cancel")
    public Result<String> batchCancelBooking(@RequestBody Map<String, List<Long>> body, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return Result.error("未登录");
            }
            
            List<Long> ids = body.get("ids");
            if (ids == null || ids.isEmpty()) {
                return Result.error("请选择要取消的预约");
            }
            
            // 管理员可以批量取消任何预约，其他用户只能取消自己相关的预约
            if ("admin".equals(user.getRole())) {
                for (Long id : ids) {
                    bookingService.updateBookingStatus(id, 2);  // 2 表示已取消
                }
            } else {
                bookingService.batchCancelBooking(ids, user.getId());
            }
            return Result.success("批量取消成功");
        } catch (Exception e) {
            return Result.error("批量取消失败：" + e.getMessage());
        }
    }
    
    @DeleteMapping("/batch")
    public Result<String> batchDeleteBooking(@RequestBody Map<String, List<Long>> body, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return Result.error("未登录");
            }
            
            List<Long> ids = body.get("ids");
            if (ids == null || ids.isEmpty()) {
                return Result.error("请选择要删除的预约");
            }
            
            // 管理员可以批量删除任何预约，其他用户只能删除自己相关的预约
            if ("admin".equals(user.getRole())) {
                for (Long id : ids) {
                    bookingService.deleteBooking(id, null);  // 管理员删除不需要检查用户ID
                }
            } else {
                bookingService.batchDeleteBooking(ids, user.getId());
            }
            return Result.success("批量删除成功");
        } catch (Exception e) {
            return Result.error("批量删除失败：" + e.getMessage());
        }
    }
} 