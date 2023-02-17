package com.green.nowon.service.mypage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.green.nowon.domain.dto.memberDTO.AddressDetailDTO;
import com.green.nowon.domain.dto.memberDTO.DepartmentDTO;
import com.green.nowon.domain.dto.memberDTO.SalaryListDTO;
import com.green.nowon.domain.dto.position.PositionDTO;
import com.green.nowon.domain.entity.cate.DepartmentEntity;
import com.green.nowon.domain.entity.cate.DepartmentEntityRepository;
import com.green.nowon.domain.entity.cate.DepartmentMemberEntity;
import com.green.nowon.domain.entity.cate.DepartmentMemberEntityRepository;
import com.green.nowon.domain.entity.cate.PositionEntity;
import com.green.nowon.domain.entity.cate.PositionRepository;
import com.green.nowon.domain.entity.member.AddressEntityRepository;
import com.green.nowon.domain.entity.member.MemberEntity;
import com.green.nowon.domain.entity.member.MemberEntityRepository;
import com.green.nowon.domain.entity.member.ProfileEntityRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MyPageServiceProcess implements MyPageService {

	private final MemberEntityRepository mRepo;

	private final AddressEntityRepository aRepo;

	private final ProfileEntityRepository proRepo;

	private final DepartmentEntityRepository dRepo;

	private final PositionRepository pRepo;

	private final DepartmentMemberEntityRepository dmRepo;

	@Transactional
	@Override
	public void info(final long mno, final Model model) {

		// 로그인한 회원의 정보 + 회원이 등록한 이미지
		model.addAttribute("detail", mRepo.findById(mno).map(SalaryListDTO::new).orElseThrow());
		// 로그인한 회원의 주소정보
		model.addAttribute("mpageaddr", aRepo.findByMemberMno(mno).map(AddressDetailDTO::new).orElseThrow());
	}

	@Transactional
	@Override // 급여관리리스트 회원정보
	public void list(final long mno, final Model model) {
		final List<SalaryListDTO> result = mRepo.findAll().stream().map(SalaryListDTO::new).collect(Collectors.toList());
		model.addAttribute("srlist", result);
	}

	@Transactional
	@Override // 급여관리리스트에서 사원의 디테일페이지
	public void salaryInfo(final long mno, final Model model) {

		// System.out.println("start : findByMember_mnoAndDepartment_depth");
		final Optional<DepartmentMemberEntity> ee = dmRepo.findByMember_mnoAndDepartment_depth(mno, 3);
		// System.out.println("end : findByMember_mnoAndDepartment_depth");

		// System.out.println(ee.get().getDepartment());
		// System.out.println(ee.get().getMember().getId());
		// System.out.println(ee.get().getMember().getMno());
		// System.out.println(ee.get().getMember().getProfile().getUrl());
		//
		// List<ProfileEntity> pfile = ee.get().getMember().getProfile();
		// System.out.println(pfile.size());
		// for(ProfileEntity p : pfile) {
		// System.out.println(p.getNewName());
		// }
		// Set<MyRole> set =ee.get().getMember().getRoles();
		// for(MyRole r : set) {
		// System.out.println(r.getRole());
		// }
		//
		// model.addAttribute("detail",
		// dmRepo.findByMember_mnoAndDepartment_depth(mno,3)
		// .map(DepartmentMemberDTO::new)
		// .orElseThrow());

		model.addAttribute("detail", mRepo.findById(mno).map(SalaryListDTO::new).orElseThrow());

		final List<DepartmentDTO> result = dRepo.findAllByDepth(3).stream().map(DepartmentDTO::new)
				.collect(Collectors.toList());
		model.addAttribute("department", result);

		final List<PositionDTO> aaa = pRepo.findAll().stream().map(PositionDTO::new).collect(Collectors.toList());
		model.addAttribute("position", aaa);
	}

	@Override
	public void update(final long mno, final long pno, final long dno) {
		final Optional<MemberEntity> me = mRepo.findById(mno);

		me.ifPresent(e -> { // 직책등록
			e.setPno(PositionEntity.builder().pno(pno).build());
			mRepo.save(e);
		});

		// 기존부서 일괄 삭제
		final List<DepartmentMemberEntity> temp = dmRepo.findAllByMember_mno(mno);
		for (final DepartmentMemberEntity d : temp)
			;
		dmRepo.deleteByMember_mno(mno);
		dmRepo.save(DepartmentMemberEntity.builder() // 부서등록
				.department(DepartmentEntity.builder().dno(dno).build())
				.member(MemberEntity.builder().mno(mno).build())
				.build());
	}

	@Transactional
	@Override
	public void update2(final long mno, final double plSal, final Integer totSal) {
		final Optional<MemberEntity> mem = mRepo.findById(mno);
		mem.ifPresent(e -> {
			e.setBoList(plSal);
			e.setTotSalary(totSal);
			mRepo.save(e);
		});

	}

}
