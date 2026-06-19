package com.example.springedu2.controller;

import com.example.springedu2.entity.Visitor;
import com.example.springedu2.repository.VisitorRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class VisitorController {


    // 1. @Autowired 이용 생성자 주입
    // @Autowired
    // private VisitorRepository visitorRepository;

    // 2. 생성자 주입 : 요즘 방식
    // private VisitorRepository visitorRepository;

    /*
    public VisitorController(VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }
    */

    // 3. 생성자 주입 다른 방법, final 가능
    // 이거 쓸라면 맨 위에 Public class 에서 @RequiredArgsConstructor 써야함 : Lombok 이 필수임
    // final이 붙는 변수는 한번만 초기화 할 수 있는 변수이다.
    private final VisitorRepository visitorRepository;


    // vlist 방명록 조회
    @GetMapping("/vlist")
    public ModelAndView vlist() {
        List<Visitor> visitors = visitorRepository.findAll(); // 목록 조회
        return visitorView(visitors, null);
    }

    // visitorView() 함수
    private ModelAndView visitorView(List<Visitor> visitors, String buttonText) {
        ModelAndView mv = new ModelAndView("visitorView"); // setViewName 안넣고 싶으면 이런식으로 파라미터 자리에 넣으면 됨
        // mv.setViewName("visitorView"); // visitorView.html(Model 사용) - thymeleaf
        if( visitors.isEmpty() ) {
            mv.addObject("msg", "조회된 결과가 없습니다.");
        } else {
            mv.addObject("vList", visitors);
        }
        if( buttonText != null ) {
            mv.addObject("buttonText", buttonText);
        }
        return mv;
    }


    // /vinsert 방명록 추가
    // @Valid : form 에서 넘어온 자료를 @Entity 에 있는 설정(@Id, @NotBlank, @Column(nullable=false))과 비교해서 입력 data 를 검증
    @PostMapping("vinsert")
    @Transactional
    public String vinsert(@Valid Visitor visitor,
                  BindingResult bindingResult,
                  Model model) {

        System.out.println("visitor: " + visitor);
        System.out.println("bindingResult: " + bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("msg", "이름과 내용을 모두 입력하세요");
            return "visitorView"; // visitorView.html
        }
        visitorRepository.save(visitor); // insert 실행 entity type
        // entity 객체를 사용해야한다. save() 여기 괄호안에 적는건 entity 타입만 적을 수 있음

        return "redirect:/vlist";

    }

    // /one 방명록 id로 조회 : Rest 방식 호출 -> 결과가 json
    // return 되는 값이 Visitor 객체일 때 이것은 json 으로 변경되어 다운로드 된다.
    // return 값이 ResponseEntity<Visitor> 일 때 data는 json 으로 상태코드로 return 가능
    // http://localhost:9090/one?id=1
    @GetMapping(value = "/one", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseEntity<Visitor> one(@RequestParam Integer id) {
        return visitorRepository.findById(Long.valueOf(id)) // data를 id 로 조회한다. Visitor 리턴
                .map(ResponseEntity::ok) // 상태 코드 ok 200을 추가해서 리턴
                .orElseGet( () -> ResponseEntity.notFound().build() );
                  // 못찾으면 null 대신에 404 코드를 객체로 바꾸어서 -> .build() 리턴
    }
    
    /*
    // /vupdate 수정
    @PostMapping("/vupdate")
    public String vupdate(@Valid Visitor visitor,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        
        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("msg", "수정할 이름과 내용을 모두 입력하세요");
            return "redirect:/vlist";
        }
        // 수정
        visitorRepository.save(visitor);
        return "redirect:/vlist";
    }
    */
    
    // /vupdate 수정
    @PostMapping("/vupdate")
    @Transactional // 일단 이거 필수고, 트랜젝셔널 > 이게 은행 송금 로직 등에서 양쪽에서 ok가 되어야 반영이 되는데 약간 그런쪽을 맡는 놈임
    // ㄴ 이거 안쓸거면 .save 를 써야함 (바로 위에 .save 쓰니까 트랜젝셔널 안쓰잖음
    public String vupdate(@Valid Visitor visitor,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if( bindingResult.hasErrors() ) {
            redirectAttributes.addFlashAttribute("msg", "수정할 이름과 내용을 모두 입력하세요");
            return "redirect:/vlist";
        }
        Visitor entity = visitorRepository.findById( Long.valueOf( visitor.getId() ) )
                .orElseThrow( () -> new IllegalArgumentException("존재하지 않는 방명록입니다.") );
        entity.setName(visitor.getName());
        entity.setMemo(visitor.getMemo());
        return "redirect:/vlist";
    }

    // /vdelete
    @PostMapping("/vdelete")
    @Transactional
    public String vdelete(@RequestParam Integer id, RedirectAttributes redirectAttributes) {
        if( !visitorRepository.existsById( Long.valueOf(id) ) ) {
            redirectAttributes.addFlashAttribute("msg", "삭제할 방명록이 없습니다.");
            return "redirect:/vlist";
        }
        visitorRepository.deleteById(Long.valueOf(id));
        return "redirect:/vlist";
    }

    // /vsearch
    // findByMemoContainingIgnoreCaseOrderByIdDesc
    // 검색 : 모두 대문자로 검색어를 포함한 data
    // 단 정렬은 id를 내림차순으로 출력한다.
    @GetMapping("/vsearch")
    public ModelAndView search(@RequestParam(defaultValue = "") String key) {
        List<Visitor> visitors = key.isBlank()
                ? visitorRepository.findAll()
                : visitorRepository.findByIrum(key);
                // : visitorRepository.findByMemoContainingIgnoreCaseOrderByIdDesc(key);
                // : visitorRepository.findByName(key);
        System.out.println(visitors);
        return visitorView(visitors, "메인으로 돌아가기");
    }

} // Controller End
