import areaData from 'china-area-data/data'

// 转换数据格式为级联选择器需要的格式
function transformData() {
  const provinces = areaData['86']  // 获取省份数据
  return Object.keys(provinces).map(provinceCode => {
    const province = {
      value: provinces[provinceCode],
      label: provinces[provinceCode],
      children: []
    }

    // 获取城市数据
    const cities = areaData[provinceCode]
    if (cities) {
      province.children = Object.keys(cities).map(cityCode => {
        const city = {
          value: cities[cityCode],
          label: cities[cityCode],
          children: []
        }

        // 获取区县数据
        const districts = areaData[cityCode]
        if (districts) {
          city.children = Object.keys(districts).map(districtCode => ({
            value: districts[districtCode],
            label: districts[districtCode]
          }))
        }

        return city
      })
    }

    return province
  })
}

// 导出转换后的数据
export const cascaderOptions = transformData() 