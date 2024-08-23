package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    // 日報一覧画面
    @GetMapping
    public String list(Model model) {

        model.addAttribute("listSize");
        model.addAttribute("reportsList");

        return "reports/list";
    }

    // 日報詳細画面
    @GetMapping(value = "/1")
    public String detail(Model model) {

        model.addAttribute("report");
        return "reports/detail";
    }

    // 日報更新画面を表示
    @GetMapping("/1/update")
    public String edit(Model model, Report report) {
        model.addAttribute("report");
        // 日報更新画面に遷移
        return "reports/update";
    }

    // 日報更新処理
    @PostMapping("/1/update")
    public String update(@Validated Report report, BindingResult res, Model model) {

     // 入力チェック
        if (res.hasErrors()) {
            return create(report);
        }
        // 一覧画面にリダイレクト
        return "redirect:/reports";

    }

    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Report report) {

        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Report report, BindingResult res, Model model) {



        // 入力チェック
        if (res.hasErrors()) {
            return create(report);
        }
        // 一覧画面にリダイレクト
        return "redirect:/reports";

    }


    // 従業員削除処理
    @PostMapping(value = "/1/delete")
    public String delete(@AuthenticationPrincipal UserDetail userDetail, Model model) {
        return "redirect:/reports";
    }
}
